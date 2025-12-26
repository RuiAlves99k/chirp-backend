package com.ruialves.chirp.domain.exception

import com.ruialves.chirp.domain.type.UserId

class ChatParticipantNotFoundException(
    id: UserId
): RuntimeException("The chat participant with the ID $id was not found")