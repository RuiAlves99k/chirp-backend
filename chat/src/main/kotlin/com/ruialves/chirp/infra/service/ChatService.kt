package com.ruialves.chirp.infra.service

import com.ruialves.chirp.api.dto.ChatMessageDto
import com.ruialves.chirp.api.mappers.toChatMessageDto
import com.ruialves.chirp.domain.event.ChatCreatedEvent
import com.ruialves.chirp.domain.event.ChatParticipantsJoinedEvent
import com.ruialves.chirp.domain.event.ChatParticipantsLeftEvent
import com.ruialves.chirp.domain.exception.ChatNotFoundException
import com.ruialves.chirp.domain.exception.ChatParticipantNotFoundException
import com.ruialves.chirp.domain.exception.ForbiddenException
import com.ruialves.chirp.domain.exception.InvalidChatSizeException
import com.ruialves.chirp.domain.models.Chat
import com.ruialves.chirp.domain.models.ChatMessage
import com.ruialves.chirp.domain.type.ChatId
import com.ruialves.chirp.domain.type.UserId
import com.ruialves.chirp.infra.database.entities.ChatEntity
import com.ruialves.chirp.infra.database.mappers.toChat
import com.ruialves.chirp.infra.database.mappers.toChatMessage
import com.ruialves.chirp.infra.database.repositories.ChatMessageRepository
import com.ruialves.chirp.infra.database.repositories.ChatParticipantRepository
import com.ruialves.chirp.infra.database.repositories.ChatRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val chatParticipantsRepository: ChatParticipantRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
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
        return chatRepository.saveAndFlush(
            ChatEntity(
                creator = creator,
                participants = setOf(creator) + otherParticipants
            )
        ).toChat(lastMessage = null).also { entity ->
            applicationEventPublisher.publishEvent(
                ChatCreatedEvent(
                    chatId = entity.id,
                    participantIds = entity.participants.map { it.userId }
                )
            )
        }
    }

    @Transactional
    fun addParticipantsToChat(
        requestUserId: UserId,
        chatId: ChatId,
        userIds: Set<UserId>,
    ): Chat {
        val chat = chatRepository.findByIdOrNull(chatId)
            ?: throw ChatNotFoundException()

        val isRequestingUserInChat = chat.participants.any {
            it.userId == requestUserId
        }
        if (!isRequestingUserInChat) {
            throw ForbiddenException()
        }

        val users = userIds.map { userId ->
            chatParticipantsRepository.findByIdOrNull(userId)
                ?: throw ChatParticipantNotFoundException(userId)
        }

        val lastMessage = lastMessageForChat(chatId)
        val updatedChat = chatRepository.save(
            chat.apply {
                this.participants = chat.participants + users
            }
        ).toChat(lastMessage)

        applicationEventPublisher.publishEvent(
            ChatParticipantsJoinedEvent(
                chatId = chatId,
                userIds = userIds
            )
        )

        return updatedChat
    }


    @Transactional
    fun removeParticipantFromChat(
        chatId: ChatId,
        userId: UserId,
    ) {
        val chat = chatRepository.findByIdOrNull(chatId)
            ?: throw ChatNotFoundException()

        val participant = chat.participants.find { it.userId == userId }
            ?: throw ChatParticipantNotFoundException(userId)

        val newParticipantSize = chat.participants.size - 1
        if (newParticipantSize == 0) {
            chatRepository.deleteById(chatId)
            return
        }

        chatRepository.save(
            chat.apply {
                this.participants = chat.participants - participant
            }
        )

        applicationEventPublisher.publishEvent(
            ChatParticipantsLeftEvent(
                chatId = chatId,
                userId = userId
            )
        )
    }


    private fun lastMessageForChat(chatId: ChatId): ChatMessage? {
        return chatMessageRepository
            .findLatestMessagesByChatIds(setOf(chatId))
            .firstOrNull()
            ?.toChatMessage()
    }

    @Cacheable(
        value = ["messages"],
        key = "#chatId",
        condition = "before == null && #pageSize <= 50",
        sync = true
    )
    fun getChatMessages(
        chatId: ChatId,
        before: Instant?,
        pageSize: Int,
    ): List<ChatMessageDto> {
        return chatMessageRepository
            .findByChatIdBefore(
                chatId = chatId,
                before = before ?: Instant.now(),
                pageable = PageRequest.of(0, pageSize)
            ).content
            .asReversed()
            .map { it.toChatMessage().toChatMessageDto() }
    }

    fun getChatById(chatId: ChatId, requestUserId: UserId): Chat? {
        return chatRepository.findChatById(chatId, requestUserId)?.toChat(lastMessageForChat(chatId))
    }

    fun findChatsByUser(userId: UserId): List<Chat> {
        val chatEntities = chatRepository.findAllByUserId(userId)
        val chatIds = chatEntities.mapNotNull { it.id }
        val latestMessages = chatMessageRepository
            .findLatestMessagesByChatIds(chatIds.toSet())
            .associateBy { it.chatId }

        return chatEntities.map {
            it.toChat(
                lastMessage = latestMessages[it.id]?.toChatMessage()
            )
        }
            .sortedByDescending { it.lastActivityAt }
    }
}