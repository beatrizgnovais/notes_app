package com.beatrizgnovais.adapter.output.cache

import com.beatrizgnovais.application.port.output.PdfCachePort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * Adapter de saida que implementa PdfCachePort usando Redis.
 *
 * Usa um RedisTemplate<String, ByteArray> dedicado (configurado em CacheConfig)
 * para guardar os bytes brutos do PDF sem serializacao JSON, o que e mais eficiente
 * para dados binarios.
 *
 * Chaves no Redis seguem o padrao "pdf::{id}", ex: "pdf::42"
 * TTL de 1 hora — o PDF so muda se a nota for atualizada, o que dispara evict().
 */
@Component
class RedisPdfCacheAdapter(
    private val pdfRedisTemplate: RedisTemplate<String, ByteArray>
) : PdfCachePort {

    companion object {
        private const val KEY_PREFIX = "pdf::"
        private val TTL = Duration.ofHours(1)
    }

    override fun get(id: Long): ByteArray? =
        pdfRedisTemplate.opsForValue().get("$KEY_PREFIX$id")

    override fun set(id: Long, pdfBytes: ByteArray) {
        pdfRedisTemplate.opsForValue().set("$KEY_PREFIX$id", pdfBytes, TTL)
    }

    override fun evict(id: Long) {
        pdfRedisTemplate.delete("$KEY_PREFIX$id")
    }
}
