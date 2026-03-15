package com.ruialves.chirp.api.dto.ws

import com.ruialves.chirp.domain.type.UserId

data class ProfilePictureUpdateDto(
    val userId: UserId,
    val newUrl: String?
)
