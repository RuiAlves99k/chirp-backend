package com.ruialves.chirp.api.dto

import com.ruialves.chirp.domain.model.DeviceToken
import jakarta.validation.constraints.NotBlank

data class RegisterDeviceRequest(
    @field:NotBlank
    val token: String,
    val platform: PlatformDto
)

enum class PlatformDto {
    ANDROID, IOS
}
