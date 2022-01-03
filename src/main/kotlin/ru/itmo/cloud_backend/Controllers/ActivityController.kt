package ru.itmo.cloud_backend.Controllers

import com.google.cloud.storage.Storage
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.format.Format
import com.sksamuel.scrimage.format.FormatDetector
import com.sksamuel.scrimage.nio.ImageWriter
import com.sksamuel.scrimage.nio.JpegWriter
import com.sksamuel.scrimage.nio.PngWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.itmo.cloud_backend.DTO.ActivityDTO
import ru.itmo.cloud_backend.Models.Activity
import ru.itmo.cloud_backend.Models.ImageProcessed
import ru.itmo.cloud_backend.Models.ImageSizeType
import ru.itmo.cloud_backend.Models.Result
import ru.itmo.cloud_backend.Services.ActivityService
import ru.itmo.cloud_backend.Services.ResultService
import ru.itmo.cloud_backend.Services.UserService
import java.awt.Color
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@RestController()
@RequestMapping("/api/activities")
class ActivityController {

    @Autowired
    lateinit var activityService: ActivityService

    @Autowired
    lateinit var resultService: ResultService

    @Autowired
    lateinit var userService: UserService

    @Autowired
    private lateinit var storage: Storage

    @Value("\${gcs-resource-test-bucket}")
    private val bucketName: String = ""

    @PostMapping
    fun addActivity(
        @RequestPart("activity") activityDTO: ActivityDTO,
        @RequestPart("image") imageFile: MultipartFile
    ): ActivityDTO {
        val activity = activityService.addActivity(activityDTO,imageFile)
        activityDTO.id = activity.id
        activityDTO.posterUrl = "https://storage.googleapis.com/${activity.poster_url}"
        activityDTO.miniPosterUrl = "https://storage.googleapis.com/${activity.mini_poster_url}"
        return activityDTO
    }

    @DeleteMapping("/{id}")
    fun deleteActivity(@PathVariable id: Long) {
        activityService.findAndDelete(id)

    }

    @PutMapping("/{id}")
    fun updateActivity(
        @PathVariable id: Long,
        @RequestPart("activity") activityDTO: ActivityDTO,
        @RequestPart("image") imageFile: MultipartFile?
    ) {
        val activity = activityService.findById(id).get()
        val newActivity = Activity(activityDTO, activity.poster_url,activity.mini_poster_url, userService.getCurrentUser())
        if (newActivity.stages.size != activity.stages.size && activity.results != null && activity.results!!.size > 0)
            throw Exception("The amount of stages should be ${activity.stages.size}")
        var imageProcessed:ImageProcessed? = null
        if ( imageFile != null) {
            imageProcessed = activityService.processImage(imageFile, ImageSizeType.IMAGE_POSTER)
            var bucket = storage.get(bucketName)
            val blob = bucket!!.create(
                imageProcessed.name,
                imageProcessed.image.bytes(imageProcessed.writer),
                imageProcessed.contentType
            )
                ?: throw Exception("Didn's save the image")
        }
        activity.apply {
            name = newActivity.name
            description = newActivity.description
            if (newActivity.stages.size == activity.stages.size)
                newActivity.stages.forEachIndexed { index, value ->
                    stages[index] = value
                }
            else
                stages = newActivity.stages
            if (imageProcessed != null)
                poster_url = "$bucketName/${imageProcessed.name}"
        }
    }

    @GetMapping
    fun findActivities(@RequestParam("name", required = false) name: String?, pageable: Pageable): List<ActivityDTO> {
        var activities: List<Activity>
        if (name == null)
            activities = activityService.findAll(pageable).toList()
        else
            activities = activityService.findActivitiesByNameIgnoreCaseContaining(name, pageable).toList()
        return activities.map { it -> ActivityDTO.fromActivity(it) }
    }

    @PostMapping("/{id}")
    fun startRun(@PathVariable id: Long):Result {
        return activityService.startRun(id)
    }
}