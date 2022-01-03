package ru.itmo.cloud_backend.Services

import com.google.cloud.storage.Storage
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.format.Format
import com.sksamuel.scrimage.format.FormatDetector
import com.sksamuel.scrimage.nio.ImageWriter
import com.sksamuel.scrimage.nio.JpegWriter
import com.sksamuel.scrimage.nio.PngWriter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ru.itmo.cloud_backend.DTO.ActivityDTO
import ru.itmo.cloud_backend.Models.*
import ru.itmo.cloud_backend.Repositoryes.ActivityRepository
import ru.itmo.cloud_backend.exceptions.BadRequestException
import ru.itmo.cloud_backend.exceptions.NotFoundException
import java.awt.Color

@Service
class ActivityService(
    private val activityRepository: ActivityRepository
) : ActivityRepository by activityRepository {

    @Value("\${gcs-resource-test-bucket}")
    private val bucketName: String = ""

    @Autowired
    private lateinit var storage: Storage

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var resultService: ResultService

    fun addActivity(activityDTO: ActivityDTO, imageFile: MultipartFile): Activity {
        val imageProcessed = processImage(imageFile, ImageSizeType.IMAGE_POSTER)
        val imageProcessedSmall = processImage(imageFile, ImageSizeType.IMAGE_SMALL_POSTER)
        var bucket = storage.get(bucketName)
        val blob = bucket!!.create(
            imageProcessed.name,
            imageProcessed.image.bytes(imageProcessed.writer),
            imageProcessed.contentType
        )
            ?: throw BadRequestException("Didn's save the image")
        val blob_small = bucket.create(
            imageProcessedSmall.name,
            imageProcessedSmall.image.bytes(imageProcessedSmall.writer),
            imageProcessedSmall.contentType
        )
            ?: throw BadRequestException("Didn's save the image small")
        var activity = Activity(
            activityDTO,
            "$bucketName/${imageProcessed.name}",
            "$bucketName/${imageProcessedSmall.name}",
            userService.getCurrentUser()
        )
        activity = save(
            activity
        )
        for (stage in activityDTO.stages)
            activity.stages.add(Stage(stage, activity))
        return save(activity)

    }

    fun processImage(imageFile: MultipartFile, imageSizeType: ImageSizeType): ImageProcessed {
        val format: Format
        try {
            format = FormatDetector.detect(imageFile.bytes).get()
        } catch (e: Exception) {
            throw BadRequestException("File should be image with png or jpeg format")
        }
        val image: ImmutableImage
        if (imageSizeType == ImageSizeType.IMAGE_POSTER)
            image = ImmutableImage.loader().fromBytes(imageFile.bytes).fit(800, 1200, Color.BLACK)
        else
            image = ImmutableImage.loader().fromBytes(imageFile.bytes).fit(160, 240, Color.BLACK)
        val writer: ImageWriter
        val contentType: String
        var imageName =
            imageFile.originalFilename!!.replace(" ", "_").substring(0, imageFile.originalFilename!!.lastIndexOf("."))
        imageName += "_" + java.time.format.DateTimeFormatter
            .ofPattern("yy-MM-dd_HH:mm:ss")
            .withZone(java.time.ZoneOffset.UTC)
            .format(java.time.Instant.now())
        if (format == Format.PNG) {
            writer = PngWriter().withCompression(8)
            contentType = "image/png"
            if (imageSizeType == ImageSizeType.IMAGE_POSTER)
                imageName += ".png"
            else
                imageName += "_small.png"
        } else {
            writer = JpegWriter().withCompression(80).withProgressive(true)
            contentType = "image/jpeg"
            if (imageSizeType == ImageSizeType.IMAGE_POSTER)
                imageName += ".jpeg"
            else
                imageName += "_small.jpeg"
        }
        return ImageProcessed(name = imageName, image, writer, contentType)
    }

    fun findAndDelete(id: Long) {
        var activity: Activity?
        val user = userService.getCurrentUser()
        try {
            activity = getById(id)
        } catch (exception: Exception) {
            throw NotFoundException("Not found activity with $id")
        }
        val name = activity.poster_url.split("/")[1]
        val nameSmall = activity.mini_poster_url.split("/")[1]
        println(name)
        storage.delete(bucketName, name)
        storage.delete(bucketName, nameSmall)
//        blob.delete()
        delete(activity)
    }

    fun startRun(id: Long): Result {
        val user = userService.getCurrentUser()
        var activity: Activity?
        try {
            activity = getById(id)
        } catch (exception: Exception) {
            throw NotFoundException("Not found activity with $id")
        }
        var result = Result(user = user, activity = activity)
        activity.results.add(result)
        activity = save(activity)
//        result = resultService.save(result)
        return activity.results.first()
//        return resultService.save(Result(user = user, activity = getById(id)))
    }

}