package com.ruialves.chirp.api.mappers

import com.ruialves.chirp.api.dto.AuthenticatedUserDto
import com.ruialves.chirp.api.dto.UserDto
import com.ruialves.chirp.domain.model.AuthenticatedUser
import com.ruialves.chirp.domain.model.User

fun AuthenticatedUser.toAuthenticatedUserDto(): AuthenticatedUserDto {
    return AuthenticatedUserDto(
        user = user.toUserDto(),
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
}

fun User.toUserDto(): UserDto{
    return UserDto(
        id = id,
        email = email,
        username = username,
        hasVerifiedEmail = hasEmailVerified,
    )
}