package com.ruialves.chirp.service

import com.ruialves.chirp.domain.events.user.UserEvent
import com.ruialves.chirp.domain.exception.InvalidCredentialsException
import com.ruialves.chirp.domain.exception.InvalidTokenException
import com.ruialves.chirp.domain.exception.SamePasswordException
import com.ruialves.chirp.domain.exception.UserNotFoundException
import com.ruialves.chirp.domain.type.UserId
import com.ruialves.chirp.infra.database.entities.PasswordResetTokenEntity
import com.ruialves.chirp.infra.message_queue.EventPublisher
import com.ruialves.chirp.infra.repositories.PasswordResetTokenRepository
import com.ruialves.chirp.infra.repositories.RefreshTokenRepository
import com.ruialves.chirp.infra.repositories.UserRepository
import com.ruialves.chirp.infra.security.PasswordEncoder
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class PasswordResetService(
    private val userRepository: UserRepository,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    @param:Value("\${chirp.email.verification.expiry-minutes}")
    private val expiryMinutes: Long,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val eventPublisher: EventPublisher,
) {

    @Transactional
    fun requestPasswordReset(email: String) {
        val user = userRepository.findByEmail(email) ?: return

        passwordResetTokenRepository.invalidateActiveTokensForUser(user)

        val token = PasswordResetTokenEntity(
            user = user,
            expiresAt = Instant.now().plus(expiryMinutes, ChronoUnit.MINUTES)
        )
        passwordResetTokenRepository.save(token)

        eventPublisher.publish(
            event = UserEvent.RequestResetPassword(
                userId = user.id!!,
                email = user.email,
                username = user.username,
                verificationToken = token.token,
                expiresInMinutes = expiryMinutes,
            )
        )
    }

    @Transactional
    fun resetPassword(token: String, newPassword: String) {
        val resetToken = passwordResetTokenRepository.findByToken(token)
            ?: throw InvalidTokenException("Invalid password reset token")

        if (resetToken.isUsed) {
            throw InvalidTokenException("Password reset token is already used")
        }

        if (resetToken.isExpired) {
            throw InvalidTokenException("Password reset token has already expired.")
        }

        val user = resetToken.user

        if (passwordEncoder.matches(newPassword, user.hashedPassword)) {
            throw SamePasswordException()
        }
        val hashedNewPassword = passwordEncoder.encode(newPassword)
        userRepository.save(
            user.apply {
                this.hashedPassword = hashedNewPassword
            }
        )

        passwordResetTokenRepository.save(
            resetToken.apply {
                this.usedAt = Instant.now()
            }
        )

        passwordResetTokenRepository.invalidateActiveTokensForUser(user)

        refreshTokenRepository.deleteByUserId(user.id!!)
    }

    @Transactional
    fun changePassword(
        userId: UserId,
        oldPassword: String,
        newPassword: String,
    ) {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()

        if (!passwordEncoder.matches(oldPassword, user.hashedPassword)) {
            throw InvalidCredentialsException()
        }

        if (oldPassword == newPassword) {
            throw SamePasswordException()
        }

        refreshTokenRepository.deleteByUserId(user.id!!)

        val newHashedPassword = passwordEncoder.encode(newPassword)
        userRepository.save(
            user.apply {
                hashedPassword = newHashedPassword
            }
        )
    }

    @Scheduled(cron = "0 0 3 * * *")
    fun cleanupExpiredTokens() {
        passwordResetTokenRepository.deleteByExpiresAtLessThan(
            now = Instant.now()
        )
    }
}