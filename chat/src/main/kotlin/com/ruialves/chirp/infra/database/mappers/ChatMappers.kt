package com.ruialves.chirp.infra.database.mappers

import com.ruialves.chirp.domain.models.Chat
import com.ruialves.chirp.domain.models.ChatMessage
import com.ruialves.chirp.domain.models.ChatParticipant
import com.ruialves.chirp.infra.database.entities.ChatEntity
import com.ruialves.chirp.infra.database.entities.ChatParticipantEntity

fun ChatEntity.toChat(lastMessage: ChatMessage? = null): Chat {
    return Chat(
        id = id!!,
        participants = participants.map {
            it.toChatParticipant()
        }.toSet(),
        creator = creator.toChatParticipant(),
        lastActivityAt = lastMessage?.createdAt ?: createdAt,
        lastMessage = lastMessage,
        createdAt = createdAt
    )
}

fun ChatParticipantEntity.toChatParticipant(): ChatParticipant {
    return ChatParticipant(
        userId = userId,
        username = username,
        email = email,
        profilePictureUrl = profilePictureUrl
    )
}