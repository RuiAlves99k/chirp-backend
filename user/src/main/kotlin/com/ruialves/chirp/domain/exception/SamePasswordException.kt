package com.ruialves.chirp.domain.exception

import java.lang.RuntimeException

class SamePasswordException: RuntimeException("The password cannot be the same one")