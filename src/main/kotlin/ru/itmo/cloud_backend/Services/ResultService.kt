package ru.itmo.cloud_backend.Services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.itmo.cloud_backend.Models.PushNotificationRequest
import ru.itmo.cloud_backend.Models.Record
import ru.itmo.cloud_backend.Models.Result
import ru.itmo.cloud_backend.Repositoryes.ResultRepository
import ru.itmo.cloud_backend.exceptions.BadRequestException
import ru.itmo.cloud_backend.exceptions.NotFoundException
import java.time.Instant
import java.time.ZonedDateTime

@Service
class ResultService(
    private val resultRepository: ResultRepository
) : ResultRepository by resultRepository {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var pushNotificationService: PushNotificationService

    fun postRecord(id: Long): Result {
        val timeNow = ZonedDateTime.now()
        val user = userService.getCurrentUser()
        val result: Result
        try {
            result = getById(id)
        } catch (exception: Exception) {
            throw NotFoundException("Not found Result with $id")
        }

        if (user != result.user)
            throw BadRequestException("Cannot modify others records")
        if (result.records.size == result.activity.stages.size)
            throw BadRequestException("You already finished this run")
//        var currrentTime = result.records.sumOf { it.time }
        var newTime = (timeNow.toEpochSecond() - result.startTime.toEpochSecond()).toInt() - result.totalTimeSeconds
        result.totalTimeSeconds += newTime
        result.records.add(
            Record(
                newTime,
                result.activity.stages[result.records.size],
                result
            )
        )
        if (result.records.size == result.activity.stages.size)
            result.endTime = timeNow
        val res = save(result)
        val request = PushNotificationRequest().apply {
            topic = "Activity-${res.activity.id}"
            title = "${res.user.email} has new Score"
            message = "User ${res.user.email} beat ${res.activity.name} stage '${res.records.last().stage.name}' with time: ${newTime}"
        }
        pushNotificationService.sendPushNotification(request)
        return res
    }

    fun updateRecord(id: Long, stage: Int, time: Int): Result {
        val user = userService.getCurrentUser()
        val result: Result
        try {
            result = getById(id)
        } catch (exception: Exception) {
            throw NotFoundException("Not found activity with $id")
        }
        if (user != result.user)
            throw BadRequestException("Cannot modify others records")
        if (result.records.size != stage) {
            if (result.records.size - 1 == stage) {
                val record = result.records.last()
                result.totalTimeSeconds = result.totalTimeSeconds - record.time + time
                record.time = time
                if (result.activity.stages.size - 1 == stage && result.records.size == result.activity.stages.size)
                    result.endTime = ZonedDateTime.ofInstant(
                        Instant.ofEpochSecond(result.startTime.toEpochSecond() + result.totalTimeSeconds.toLong()),
                        result.startTime.zone
                    )
                val res = save(result)
                val request = PushNotificationRequest().apply {
                    topic = "Activity-${res.activity.id}"
                    title = "${res.user.email} has new Score"
                    message = "User ${res.user.email} beat ${res.activity.name} stage '${res.records.last().stage.name}' with time: ${time}"
                }
                pushNotificationService.sendPushNotification(request)
                return res
            }
            if (result.records.size == 0)
                throw BadRequestException("You need to start run with stage 0")
            throw BadRequestException("Cannot modify $stage stage, only last added one (${result.records.size - 1}) or add new")
        }
        if (stage >= result.activity.stages.size)
            throw BadRequestException("You cannot add more than ${result.activity.stages.size}")
        result.records.add(Record(time, result.activity.stages[stage], result))
        result.totalTimeSeconds += time
        if (result.activity.stages.size - 1 == stage && result.records.size == result.activity.stages.size)
//            result.endTime = ZonedDateTime.now()
            result.endTime = ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(result.startTime.toEpochSecond() + result.totalTimeSeconds.toLong()),
                result.startTime.zone
            )
        val res = save(result)
        val request = PushNotificationRequest().apply {
            topic = "Activity-${res.activity.id}"
            title = "${res.user.email} has new Score"
            message = "User ${res.user.email} beat ${res.activity.name} stage '${res.records.last().stage.name}' with time: ${time}"
        }
        pushNotificationService.sendPushNotification(request)
        return res
    }
}