package ru.itmo.cloud_backend.Services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.itmo.cloud_backend.Models.PushNotificationRequest


@Service
class PushNotificationService(private val fcmService: FCMService) {
    private val logger: Logger = LoggerFactory.getLogger(PushNotificationService::class.java)
    fun sendPushNotification(request: PushNotificationRequest) {
        try {
            fcmService.sendMessage(samplePayloadData, request)
        } catch (e: Exception) {

            logger.error(e.message)
            throw e
        }
    }

    fun sendPushNotificationWithoutData(request: PushNotificationRequest) {
        try {
            fcmService.sendMessageWithoutData(request)
        } catch (e: Exception) {
            logger.error(e.message)
        }
    }

    fun sendPushNotificationToToken(request: PushNotificationRequest) {
        try {
            fcmService.sendMessageToToken(request)
        } catch (e: Exception) {
            logger.error(e.message)
        }
    }

    private val samplePayloadData: Map<String, String>
        private get() {
            val pushData: MutableMap<String, String> = HashMap()
            pushData["messageId"] = "msgid"
            pushData["text"] = "txt"
            pushData["user"] = "pankaj singh"
            return pushData
        }

}