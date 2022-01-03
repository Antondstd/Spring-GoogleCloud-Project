package ru.itmo.cloud_backend.Controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import ru.itmo.cloud_backend.Models.PushNotificationRequest
import ru.itmo.cloud_backend.Models.Record
import ru.itmo.cloud_backend.Models.Result
import ru.itmo.cloud_backend.Services.*
import ru.itmo.cloud_backend.exceptions.BadRequestException
import java.time.Instant
import java.time.ZonedDateTime

@RestController()
@RequestMapping("/api/results")
class ResultController {

    @Autowired
    lateinit var activityService: ActivityService

    @Autowired
    lateinit var resultService: ResultService

    @Autowired
    lateinit var recordService: RecordService

    @Autowired
    lateinit var userService: UserService

    @PostMapping("/{id}")
    fun postRecord(@PathVariable id: Long): Result {
        return resultService.postRecord(id)
    }

    @PutMapping("/{id}")
    fun addRecord(@PathVariable id: Long, @RequestParam stage: Int, @RequestBody time: Int): Result {
//        val user = userService.getCurrentUser()
//        val result = resultService.findById(id).get()
//        if (user != result.user)
//            throw BadRequestException("Cannot modify others records")
//        if (result.records.size != stage) {
//            if (result.records.size - 1 == stage) {
//                var record = result.records.last()
//                result.totalTimeSeconds = result.totalTimeSeconds - record.time + time
//                record.time = time
//                if (result.activity.stages.size - 1 == stage && result.records.size == result.activity.stages.size)
//                    result.endTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(result.startTime.toEpochSecond() + result.totalTimeSeconds.toLong()),result.startTime.zone)
//                return resultService.save(result)
//            } else
//                if (stage >= result.activity.stages.size)
//                    throw BadRequestException("You already finished this run")
//                throw BadRequestException("Cannot modify $stage stage, only last added one or add new")
//        }
//        result.records.add(Record(time, result.activity.stages[stage],result))
//        result.totalTimeSeconds += time
//        if (result.activity.stages.size - 1 == stage && result.records.size == result.activity.stages.size)
////            result.endTime = ZonedDateTime.now()
//            result.endTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(result.startTime.toEpochSecond() + result.totalTimeSeconds.toLong()),result.startTime.zone)
        return resultService.updateRecord(id,stage,time)
    }

    @GetMapping()
    fun getResults(@RequestParam user_id:Long?,pageable: Pageable): List<Result> {
        if (user_id != null)
            return resultService.findByUserId(user_id,pageable).toList()
        return resultService.findAll(pageable).toList()
    }

    @GetMapping("/{id}")
    fun getResult(@PathVariable id: Long, pageable: Pageable): Result {
        return resultService.findById(id).get()
    }
}