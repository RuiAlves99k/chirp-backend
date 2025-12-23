package com.ruialves.chirp.domain.exception

import java.lang.RuntimeException

class UnauthorizedException: RuntimeException("Missing auth information")