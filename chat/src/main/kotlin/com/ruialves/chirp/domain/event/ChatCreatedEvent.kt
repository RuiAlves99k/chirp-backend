package com.ruialves.chirp.domain.event

import com.ruialves.chirp.domain.type.ChatId
import com.ruialves.chirp.domain.type.UserId

data class ChatCreatedEvent(
    val chatId: ChatId,
    val participantIds: List<UserId>,
)
