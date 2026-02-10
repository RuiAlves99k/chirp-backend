package com.ruialves.chirp.domain.event

import com.ruialves.chirp.domain.type.ChatId
import com.ruialves.chirp.domain.type.ChatMessageId

data class MessageDeletedEvent(
    val chatId: ChatId,
    val messageId: ChatMessageId
)
