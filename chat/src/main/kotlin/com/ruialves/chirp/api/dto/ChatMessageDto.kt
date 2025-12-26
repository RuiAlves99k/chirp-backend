package com.ruialves.chirp.api.dto

import com.ruialves.chirp.domain.type.ChatId
import com.ruialves.chirp.domain.type.ChatMessageId
import com.ruialves.chirp.domain.type.UserId
import java.time.Instant

data class ChatMessageDto(
    val id: ChatMessageId,
    val chatId: ChatId,
    val content: String,
    val createdAt: Instant,
    val senderId: UserId
)
