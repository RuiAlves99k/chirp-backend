package com.ruialves.chirp.api.dto.ws

import com.ruialves.chirp.domain.type.ChatId
import com.ruialves.chirp.domain.type.ChatMessageId

data class DeleteMessageDto(
    val chatId: ChatId,
    val messageId: ChatMessageId
)
