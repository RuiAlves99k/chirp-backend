package com.ruialves.chirp.api.dto.ws

import com.ruialves.chirp.domain.type.ChatId

data class ChatParticipantsChangedDto(
    val chatId: ChatId
)
