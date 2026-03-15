package com.ruialves.chirp.domain.exception

class StorageException(
    override val message: String? = null
): RuntimeException(message ?: "Unable to store file")