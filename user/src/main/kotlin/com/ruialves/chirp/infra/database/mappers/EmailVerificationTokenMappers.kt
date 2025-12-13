package com.ruialves.chirp.infra.database.mappers

import com.ruialves.chirp.domain.model.EmailVerificationToken
import com.ruialves.chirp.infra.database.entities.EmailVerificationTokenEntity

fun EmailVerificationTokenEntity.toEmailVerificationToken(): EmailVerificationToken{
    return EmailVerificationToken(
        id = id,
        token = token,
        user = user.toUser(),
    )
}