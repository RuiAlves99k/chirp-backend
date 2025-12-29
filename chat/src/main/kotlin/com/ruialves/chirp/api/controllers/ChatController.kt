package com.ruialves.chirp.api.controllers

import com.ruialves.chirp.api.dto.AddParticipantToChatDto
import com.ruialves.chirp.api.dto.ChatDto
import com.ruialves.chirp.api.dto.CreateChatRequest
import com.ruialves.chirp.api.mappers.toChatDto
import com.ruialves.chirp.api.util.requestUserId
import com.ruialves.chirp.domain.type.ChatId
import com.ruialves.chirp.infra.service.ChatService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatService: ChatService
) {


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
}