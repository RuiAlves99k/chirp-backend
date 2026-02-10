package com.ruialves.chirp.infra.caching

import com.rabbitmq.client.ConnectionFactory
import com.ruialves.chirp.domain.events.ChirpEvent
import org.springframework.amqp.support.converter.JacksonJavaTypeMapper
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import tools.jackson.databind.DefaultTyping
import tools.jackson.databind.json.JsonMapper
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import tools.jackson.module.kotlin.kotlinModule
import java.time.Duration

@Configuration
@EnableCaching
class RedisConfig {

    @Bean
    fun cacheManager(
        connectionFactory: LettuceConnectionFactory
    ): RedisCacheManager {
        val polymorphicTypeValidator = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType("java.util.") // Allow Java lists
            .allowIfSubType("kotlin.collections.") // Kotlin collections
            .allowIfSubType("com.ruialves.chirp.")
            .build()


        val objectMapper = JsonMapper.builder()
            .addModule(kotlinModule())
            .polymorphicTypeValidator(polymorphicTypeValidator)
            .activateDefaultTyping(polymorphicTypeValidator, DefaultTyping.NON_FINAL)
            .build()

        val cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(TTL_HOURS))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    GenericJacksonJsonRedisSerializer(objectMapper)
                )
            )

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(cacheConfig)
            .withCacheConfiguration(
                "messages",
                cacheConfig.entryTtl(Duration.ofMinutes(30L))
            )
            .transactionAware()
            .build()
    }

    companion object {
        private const val TTL_HOURS = 1L
    }
}