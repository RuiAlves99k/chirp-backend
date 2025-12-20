package com.ruialves.chirp.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.ruialves.chirp.api.util.Password
import jakarta.validation.constraints.Email
import org.hibernate.validator.constraints.Length

data class RegisterRequest(
    @JsonProperty("email")
    @field:Email(message = "Must be a valid email address")
    val email: String,
    @JsonProperty("username")
    @field:Length(min = 3, max = 20, message = "Username length must be between 3 and 20 characters")
    val username: String,
    @JsonProperty("password")
    @field:Password
    val password: String,
)