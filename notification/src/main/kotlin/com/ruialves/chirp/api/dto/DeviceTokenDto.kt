package com.ruialves.chirp.api.dto

import com.ruialves.chirp.domain.type.UserId
import java.time.Instant

data class DeviceTokenDto(
    val userId: UserId,
    val token: String,
    val createdAt: Instant = Instant.now()
)
