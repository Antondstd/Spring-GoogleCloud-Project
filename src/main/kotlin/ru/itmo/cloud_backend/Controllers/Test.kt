package ru.itmo.cloud_backend.Controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.itmo.cloud_backend.Models.PushNotificationRequest
import ru.itmo.cloud_backend.Services.PushNotificationService




@RestController()
@RequestMapping("/")
class Test {
    @GetMapping
    fun test():String{
        return "test"
    }
    @GetMapping("/liveness_check")
    fun testLiveness(){

    }
    @GetMapping("/readiness_check")
    fun readLiveness(){

    }

    @Autowired
    lateinit var pushNotificationService: PushNotificationService

    @GetMapping("/test_notification")
    fun sendNotification():String{
        val request = PushNotificationRequest().apply {
            topic = "Test"
            title = "Title"
            message = "Message test"
        }
        pushNotificationService.sendPushNotification(request)
        return "sended"
    }
}