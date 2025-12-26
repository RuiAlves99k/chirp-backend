package com.ruialves.chirp.infra.service

import com.ruialves.chirp.domain.exception.ChatParticipantNotFoundException
import com.ruialves.chirp.domain.exception.InvalidChatSizeException
import com.ruialves.chirp.domain.models.Chat
import com.ruialves.chirp.domain.type.UserId
import com.ruialves.chirp.infra.database.entities.ChatEntity
import com.ruialves.chirp.infra.database.mappers.toChat
import com.ruialves.chirp.infra.database.repositories.ChatParticipantRepository
import com.ruialves.chirp.infra.database.repositories.ChatRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val chatParticipantsRepository: ChatParticipantRepository,
) {

    @Transactional
    fun createChat(
        creatorId: UserId,
        otherUserIds: Set<UserId>
    ): Chat {
        val otherParticipants = chatParticipantsRepository.findByUserIdIn(
            userIds = otherUserIds
        )

        val allParticipants = (otherParticipants + creatorId)
        if (allParticipants.size < 2) {
            throw InvalidChatSizeException()
        }

        val creator = chatParticipantsRepository.findByIdOrNull(creatorId)
            ?: throw ChatParticipantNotFoundException(creatorId)
        return chatRepository.save(
            ChatEntity(
                creator = creator,
                participants = setOf(creator) + otherParticipants
            )
        ).toChat(lastMessage = null)
    }

}