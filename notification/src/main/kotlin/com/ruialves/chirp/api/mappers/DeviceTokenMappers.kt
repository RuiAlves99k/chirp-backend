package com.ruialves.chirp.api.mappers

import com.ruialves.chirp.api.dto.DeviceTokenDto
import com.ruialves.chirp.api.dto.PlatformDto
import com.ruialves.chirp.domain.model.DeviceToken

fun DeviceToken.toDeviceTokenDto(): DeviceTokenDto {
    return DeviceTokenDto(
        userId = userId,
        token = token,
        createdAt = createdAt,
    )
}

fun PlatformDto.toPlatformDto(): DeviceToken.Platform {
    return when(this){
        PlatformDto.ANDROID -> DeviceToken.Platform.ANDROID
        PlatformDto.IOS -> DeviceToken.Platform.IOS
    }
}
