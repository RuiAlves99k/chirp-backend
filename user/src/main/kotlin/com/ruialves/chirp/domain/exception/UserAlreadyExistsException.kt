package com.ruialves.chirp.domain.exception

import java.lang.RuntimeException

class UserAlreadyExistsException: RuntimeException(
    "User already exists"
)