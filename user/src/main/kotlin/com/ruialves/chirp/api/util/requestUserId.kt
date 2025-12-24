package com.ruialves.chirp.api.util

import com.ruialves.chirp.domain.exception.UnauthorizedException
import com.ruialves.chirp.domain.type.UserId
import org.springframework.security.core.context.SecurityContextHolder

val requestUserId: UserId
    get() = SecurityContextHolder.getContext().authentication?.principal as? UserId
        ?: throw UnauthorizedException()