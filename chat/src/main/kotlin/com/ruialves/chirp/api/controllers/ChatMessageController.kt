package com.ruialves.chirp.api.controllers

import com.ruialves.chirp.api.util.requestUserId
import com.ruialves.chirp.domain.type.ChatMessageId
import com.ruialves.chirp.infra.service.ChatMessageService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/messages")
class ChatMessageController(
    private val chatMessageService: ChatMessageService
) {

    @DeleteMapping("/{messageId}")
    fun deleteMessage(
        @PathVariable messageId: ChatMessageId
    ) {
        chatMessageService.deleteMessage(
            messageId = messageId,
            requestUserId = requestUserId
        )
    }

}