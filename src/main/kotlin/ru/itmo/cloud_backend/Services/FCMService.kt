package ru.itmo.cloud_backend.Services

import com.google.firebase.messaging.*
import com.google.gson.GsonBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.itmo.cloud_backend.Models.PushNotificationRequest
import java.time.Duration
import java.util.concurrent.ExecutionException


@Service
class FCMService {
    private val logger: Logger = LoggerFactory.getLogger(FCMService::class.java)
    @Throws(InterruptedException::class, ExecutionException::class)
    fun sendMessage(data: Map<String, String>, request: PushNotificationRequest) {
        val message: Message = getPreconfiguredMessageWithData(data, request)
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonOutput = gson.toJson(message)
        val response = sendAndGetResponse(message)
        logger.info(
            "Sent message with data. Topic: " + request.topic.toString() + ", " + response + " msg " + jsonOutput
        )
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    fun sendMessageWithoutData(request: PushNotificationRequest) {
        val message: Message = getPreconfiguredMessageWithoutData(request)
        val response = sendAndGetResponse(message)
        logger.info("Sent message without data. Topic: " + request.topic.toString() + ", " + response)
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    fun sendMessageToToken(request: PushNotificationRequest) {
        val message: Message = getPreconfiguredMessageToToken(request)
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonOutput = gson.toJson(message)
        val response = sendAndGetResponse(message)
        logger.info(
            "Sent message to token. Device token: " + request.token
                .toString() + ", " + response + " msg " + jsonOutput
        )
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    private fun sendAndGetResponse(message: Message): String {
        return FirebaseMessaging.getInstance().sendAsync(message).get()
    }

    private fun getAndroidConfig(topic: String): AndroidConfig {
        return AndroidConfig.builder()
            .setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)
            .setPriority(AndroidConfig.Priority.HIGH)
            .setNotification(
                AndroidNotification.builder().setSound("default")
                    .setColor("#FFFF00").setTag(topic).build()
            ).build()
    }

    private fun getApnsConfig(topic: String): ApnsConfig {
        return ApnsConfig.builder()
            .setAps(Aps.builder().setCategory(topic).setThreadId(topic).build()).build()
    }

    private fun getPreconfiguredMessageToToken(request: PushNotificationRequest): Message {
        return getPreconfiguredMessageBuilder(request).setToken(request.token)
            .build()
    }

    private fun getPreconfiguredMessageWithoutData(request: PushNotificationRequest): Message {
        return getPreconfiguredMessageBuilder(request).setTopic(request.topic)
            .build()
    }

    private fun getPreconfiguredMessageWithData(data: Map<String, String>, request: PushNotificationRequest): Message {
        return getPreconfiguredMessageBuilder(request).putAllData(data).setTopic(request.topic)
            .build()
    }

    private fun getPreconfiguredMessageBuilder(request: PushNotificationRequest): Message.Builder {
        val androidConfig = getAndroidConfig(request.topic)
        val apnsConfig = getApnsConfig(request.topic)
        return Message.builder()
            .setApnsConfig(apnsConfig).setAndroidConfig(androidConfig).setNotification(
                Notification.builder().setTitle(request.title).setBody(request.message).build()
            )
    }
}