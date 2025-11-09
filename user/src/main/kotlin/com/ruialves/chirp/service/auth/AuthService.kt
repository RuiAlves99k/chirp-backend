package com.ruialves.chirp.service.auth

import com.ruialves.chirp.domain.exception.InvalidCredentialsException
import com.ruialves.chirp.domain.exception.UserAlreadyExistsException
import com.ruialves.chirp.domain.exception.UserNotFound
import com.ruialves.chirp.domain.model.AuthenticatedUser
import com.ruialves.chirp.domain.model.User
import com.ruialves.chirp.domain.model.UserId
import com.ruialves.chirp.infra.database.entities.RefreshTokenEntity
import com.ruialves.chirp.infra.database.entities.UserEntity
import com.ruialves.chirp.infra.database.mappers.toUser
import com.ruialves.chirp.infra.repositories.RefreshTokenRepository
import com.ruialves.chirp.infra.repositories.UserRepository
import com.ruialves.chirp.infra.security.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val refreshTokenRepository: RefreshTokenRepository,
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

    fun login(
        email: String,
        password: String,
    ): AuthenticatedUser {
        val user = userRepository.findByEmail(email)
            ?: throw InvalidCredentialsException()

        if (!passwordEncoder.matches(password, user.hashedPassword)){
            throw InvalidCredentialsException()
        }

        // TODO: Check for verified email

        return user.id?.let { userId ->
            val accessToken = jwtService.generateAccessToken(userId)
            val refreshToken = jwtService.generateRefreshToken(userId)
            storeRefreshToken(userId, refreshToken)

            AuthenticatedUser(
                user = user.toUser(),
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        } ?: throw UserNotFound()
    }

    private fun storeRefreshToken(userId: UserId, token: String){
        val hashed = hashToken(token)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.save(
            RefreshTokenEntity(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashed
            )
        )
    }

    private fun hashToken(token: String): String{
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}