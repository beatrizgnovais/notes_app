package com.beatrizgnovais.application.port.output

/**
 * Porta de saida para cache de PDFs gerados.
 *
 * Segue o padrao hexagonal: o nucleo da aplicacao define o contrato
 * sem saber qual tecnologia de cache esta sendo usada por baixo.
 * O adapter (RedisPdfCacheAdapter) e que conhece o Redis.
 */
interface PdfCachePort {
    /** Recupera os bytes do PDF cacheado para a nota com [id], ou null se nao houver cache. */
    fun get(id: Long): ByteArray?

    /** Armazena os bytes do PDF gerado para a nota com [id]. */
    fun set(id: Long, pdfBytes: ByteArray)

    /** Remove o PDF cacheado para a nota com [id]. Usado apos update ou delete. */
    fun evict(id: Long)
}
