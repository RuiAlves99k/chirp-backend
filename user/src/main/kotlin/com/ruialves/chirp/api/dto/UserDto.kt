package com.ruialves.chirp.api.dto

import com.ruialves.chirp.domain.type.UserId

data class UserDto(
    val id: UserId,
    val email: String,
    val username: String,
    val hasVerifiedEmail: Boolean,
)
