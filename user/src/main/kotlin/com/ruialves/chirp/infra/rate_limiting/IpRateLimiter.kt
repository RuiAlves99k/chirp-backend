package com.ruialves.chirp.infra.rate_limiting

import com.ruialves.chirp.domain.exception.RateLimitException
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class IpRateLimiter(
    private val redisTemplate: StringRedisTemplate,
) {
    companion object {
        private const val IP_RATE_LIMIT_PREFIX = "rate_limit:ip"
    }

    @Value("classpath:ip_rate_limit.lua")
    lateinit var rateLimiterResource: Resource

    private val rateLimitScript by lazy {
        val script = rateLimiterResource.inputStream.use {
            it.readBytes().decodeToString()
        }
        @Suppress("UNCHECKED_CAST")
        DefaultRedisScript(script, List::class.java as Class<List<Long>>)
    }

    fun <T> withIpRateLimit(
        ipAddress: String,
        resetsIn: Duration,
        maxRequestsPerIp: Int,
        uri: String,
        action: () -> T
    ) : T {
        val key = "$IP_RATE_LIMIT_PREFIX:$uri:$ipAddress"

        val result = redisTemplate.execute(
            rateLimitScript,
            listOf(key),
            maxRequestsPerIp.toString(),
            resetsIn.seconds.toString(),
        )

        val (currentCount, ttl) = result

        return if (currentCount <= maxRequestsPerIp){
            action()
        } else {
            throw RateLimitException(resetsInSeconds = ttl)
        }
    }
}