package com.beatrizgnovais.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.ByteArrayRedisSerializer
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

/**
 * Configuracao do cache Redis.
 *
 * Dois caches Spring Cache para notas:
 *   - "notes"      → nota individual por ID, TTL 10 minutos
 *   - "notes-list" → lista completa de notas,  TTL 5 minutos
 *
 * Um RedisTemplate dedicado para o cache manual de PDF (ByteArray),
 * usado pelo RedisPdfCacheAdapter com TTL de 1 hora.
 */
@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun cacheManager(redisConnectionFactory: RedisConnectionFactory): RedisCacheManager {
        val jsonSerializer = GenericJackson2JsonRedisSerializer()
        val keySerializer = StringRedisSerializer()

        val base = RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(keySerializer)
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer)
            )
            .disableCachingNullValues()

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(base)
            .withCacheConfiguration("notes", base.entryTtl(Duration.ofMinutes(10)))
            .withCacheConfiguration("notes-list", base.entryTtl(Duration.ofMinutes(5)))
            .build()
    }

    @Bean
    fun pdfRedisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, ByteArray> =
        RedisTemplate<String, ByteArray>().apply {
            connectionFactory = redisConnectionFactory
            keySerializer = StringRedisSerializer()
            valueSerializer = ByteArrayRedisSerializer()
        }
}
