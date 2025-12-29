package com.ruialves.chirp.infra.messaging

import com.ruialves.chirp.domain.events.user.UserEvent
import com.ruialves.chirp.domain.models.ChatParticipant
import com.ruialves.chirp.infra.message_queue.MessageQueues
import com.ruialves.chirp.infra.service.ChatParticipantService
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class ChatUserEventListener(
    private val chatParticipantService: ChatParticipantService,
) {

    @RabbitListener(queues = [MessageQueues.CHAT_USER_EVENTS])
    fun handleUserEvent(event: UserEvent){
        when(event){
            is UserEvent.Verified -> {
                chatParticipantService.createChatParticipant(
                    chatParticipant = ChatParticipant(
                        userId = event.userId,
                        username = event.username,
                        email = event.username,
                        profilePictureUrl = null
                    )
                )
            }
            else -> Unit
        }
    }
}