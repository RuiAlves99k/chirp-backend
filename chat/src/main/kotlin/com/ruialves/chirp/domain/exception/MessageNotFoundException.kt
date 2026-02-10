package com.ruialves.chirp.domain.exception

import com.ruialves.chirp.domain.type.ChatMessageId
import java.lang.RuntimeException

class MessageNotFoundException(messageId: ChatMessageId): RuntimeException("Message with ID $messageId not found")