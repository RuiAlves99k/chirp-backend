package com.ruialves.chirp.domain.event

import com.ruialves.chirp.domain.type.ChatId
import com.ruialves.chirp.domain.type.UserId

data class ChatParticipantsJoinedEvent(
    val chatId: ChatId,
    val userIds: Set<UserId>
)
