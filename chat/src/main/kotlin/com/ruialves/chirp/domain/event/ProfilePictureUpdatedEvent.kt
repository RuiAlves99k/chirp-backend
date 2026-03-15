package com.ruialves.chirp.domain.event

import com.ruialves.chirp.domain.type.UserId

data class ProfilePictureUpdatedEvent(
    val userId: UserId,
    val newUrl: String?,
)
