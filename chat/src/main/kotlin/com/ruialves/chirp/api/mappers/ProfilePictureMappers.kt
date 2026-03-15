package com.ruialves.chirp.api.mappers

import com.ruialves.chirp.api.dto.PictureUploadResponse
import com.ruialves.chirp.domain.models.ProfilePictureUploadCredentials

fun ProfilePictureUploadCredentials.toResponse(): PictureUploadResponse{
    return PictureUploadResponse(
        uploadUrl = uploadUrl,
        publicUrl = publicUrl,
        headers = headers,
        expiresAt = expiresAt
    )
}