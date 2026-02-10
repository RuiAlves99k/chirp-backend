package com.ruialves.chirp.domain.event

import com.ruialves.chirp.domain.type.ChatId
import com.ruialves.chirp.domain.type.UserId

data class ChatParticipantsLeftEvent(
    val chatId: ChatId,
    val userId: UserId
)
