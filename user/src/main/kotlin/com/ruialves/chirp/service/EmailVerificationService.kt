package com.ruialves.chirp.service

import com.ruialves.chirp.domain.events.user.UserEvent
import com.ruialves.chirp.domain.exception.InvalidTokenException
import com.ruialves.chirp.domain.exception.UserNotFoundException
import com.ruialves.chirp.domain.model.EmailVerificationToken
import com.ruialves.chirp.infra.database.entities.EmailVerificationTokenEntity
import com.ruialves.chirp.infra.database.mappers.toEmailVerificationToken
import com.ruialves.chirp.infra.database.mappers.toUser
import com.ruialves.chirp.infra.message_queue.EventPublisher
import com.ruialves.chirp.infra.repositories.EmailVerificationRepository
import com.ruialves.chirp.infra.repositories.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.String

@Service
class EmailVerificationService(
    private val emailVerificationRepository: EmailVerificationRepository,
    private val userRepository: UserRepository,
    @param:Value("\${chirp.email.verification.expiry-hours}") private val expiryHours: Long,
    private val eventPublisher: EventPublisher,
) {

    @Transactional
    fun createVerificationToken(email: String): EmailVerificationToken {
        val userEntity = userRepository.findByEmail(email)
            ?: throw UserNotFoundException()

        emailVerificationRepository.invalidateActiveTokensForUser(
            user = userEntity
        )

        val token = EmailVerificationTokenEntity(
            expiresAt = Instant.now().plus(expiryHours, ChronoUnit.HOURS),
            user = userEntity
        )

        return emailVerificationRepository.save(token).toEmailVerificationToken()
    }

    @Transactional
    fun verifyEmail(token: String) {
        val verificationToken = emailVerificationRepository.findByToken(token)
            ?: throw InvalidTokenException("Email verification token is invalid")

        if (verificationToken.isUsed) {
            throw InvalidTokenException("Email verification token is already used")
        }

        if (verificationToken.isExpired) {
            throw InvalidTokenException("Email verification token has already expired.")
        }

        emailVerificationRepository.save(
            verificationToken.apply {
                this.usedAt = Instant.now()
            }
        )

        userRepository.save(
            verificationToken.user.apply {
                this.hasVerifiedEmail = true
            }
        ).toUser()
    }

    @Transactional
    fun resendVerificationEmail(email: String) {
        val token = createVerificationToken(email)

        if (token.user.hasEmailVerified) {
            return
        }

        eventPublisher.publish(
            UserEvent.RequestResendVerification(
                userId = token.user.id,
                email = token.user.email,
                username = token.user.username,
                verificationToken = token.token,
            )
        )
    }

    @Scheduled(cron = "0 0 3 * * *")
    fun cleanupExpiredTokens() {
        emailVerificationRepository.deleteByExpiresAtLessThan(Instant.now())
    }
}