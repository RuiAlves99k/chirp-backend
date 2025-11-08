package com.ruialves.chirp.infra.database.mappers

import com.ruialves.chirp.domain.model.User
import com.ruialves.chirp.infra.database.entities.UserEntity

fun UserEntity.toUser(): User {
    return User(
        id = id!!,
        username = username,
        email = email,
        hasEmailVerified = hasVerifiedEmail,
    )
}