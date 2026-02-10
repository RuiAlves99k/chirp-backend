package com.ruialves.chirp.api.controllers

import com.ruialves.chirp.api.dto.AddParticipantToChatDto
import com.ruialves.chirp.api.dto.ChatDto
import com.ruialves.chirp.api.dto.ChatMessageDto
import com.ruialves.chirp.api.dto.CreateChatRequest
import com.ruialves.chirp.api.mappers.toChatDto
import com.ruialves.chirp.api.util.requestUserId
import com.ruialves.chirp.domain.type.ChatId
import com.ruialves.chirp.infra.service.ChatService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatService: ChatService
) {

    @GetMapping("/{chatId}")
    fun getChat(
        @PathVariable("chatId") chatId: ChatId,
    ): ChatDto {
        return chatService.getChatById(
            chatId = chatId,
            requestUserId = requestUserId
        )?.toChatDto() ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }

    @GetMapping
    fun getChatsForUser(): List<ChatDto> {
        return chatService.findChatsByUser(
            userId = requestUserId
        ).map { it.toChatDto() }
    }

    @PostMapping
    fun createChat(
        @Valid @RequestBody body: CreateChatRequest
    ): ChatDto {
        return chatService.createChat(
            creatorId = requestUserId,
            otherUserIds = body.otherUserIds.toSet()
        ).toChatDto()
    }

    @PostMapping("/{chatId}/add")
    fun addChatParticipant(
        @PathVariable chatId: ChatId,
        @Valid @RequestBody body: AddParticipantToChatDto,
    ): ChatDto {
        return chatService.addParticipantsToChat(
            requestUserId = requestUserId,
            chatId = chatId,
            userIds = body.userIds.toSet()
        ).toChatDto()
    }

    @PostMapping("/{chatId}/leave")
    fun leaveChat(
        @PathVariable chatId: ChatId,
    ) {
        return chatService.removeParticipantFromChat(
            userId = requestUserId,
            chatId = chatId,
        )
    }

    @GetMapping("/{chatId}/messages")
    fun getMessagesForChat(
        @PathVariable chatId: ChatId,
        @RequestParam("before", required = false) before: Instant? = null,
        @RequestParam("pageSize", required = false) pageSize: Int = DEFAULT_PAGE_SIZE
    ): List<ChatMessageDto> {
        return chatService.getChatMessages(
            chatId = chatId,
            before = before,
            pageSize = pageSize
        )
    }

    companion object {
        private const val DEFAULT_PAGE_SIZE = 20
    }
}