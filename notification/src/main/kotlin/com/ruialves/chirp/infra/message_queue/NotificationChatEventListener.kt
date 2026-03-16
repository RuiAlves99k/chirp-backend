package com.ruialves.chirp.infra.message_queue

import com.ruialves.chirp.domain.events.chat.ChatEvent
import com.ruialves.chirp.domain.events.user.UserEvent
import com.ruialves.chirp.service.EmailService
import com.ruialves.chirp.service.PushNotificationService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class NotificationChatEventListener(
    private val pushNotificationService: PushNotificationService
) {

    @RabbitListener(queues = [MessageQueues.NOTIFICATION_CHAT_EVENTS])
    fun handleChatEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.NewMessage -> {
                pushNotificationService.sendNewMessageNotifications(
                    recipientUserIds = event.recipientIds.toList(),
                    senderUserId = event.senderId,
                    senderUsername = event.senderUsername,
                    message = event.message,
                    chatId = event.chatId
                )
            }
        }
    }
}