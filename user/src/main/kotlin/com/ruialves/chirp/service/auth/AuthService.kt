package com.ruialves.chirp.service.auth

import com.ruialves.chirp.domain.exception.UserAlreadyExistsException
import com.ruialves.chirp.domain.model.User
import com.ruialves.chirp.infra.database.entities.UserEntity
import com.ruialves.chirp.infra.database.mappers.toUser
import com.ruialves.chirp.infra.repositories.UserRepository
import com.ruialves.chirp.infra.security.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun register(email: String, username: String, password: String): User {
        val user = userRepository.findByEmailOrUsername(
            email = email.trim(),
            username = username.trim()
        )
        if (user != null){
            throw UserAlreadyExistsException()
        }

        val savedUser = userRepository.save(
            UserEntity(
                email = email.trim(),
                username = username.trim(),
                hashedPassword = passwordEncoder.encode(password),
            )
        ).toUser()

        return savedUser
    }
}