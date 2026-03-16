package com.ruialves.chirp.infra.mappers

import com.ruialves.chirp.domain.model.DeviceToken
import com.ruialves.chirp.infra.database.DeviceTokenEntity

fun DeviceTokenEntity.toDeviceToken(): DeviceToken {
    return DeviceToken(
        id = id,
        userId = userId,
        token = token,
        platform = platform.toPlatform(),
        createdAt = createdAt
    )
}