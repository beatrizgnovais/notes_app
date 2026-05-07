package com.beatrizgnovais.domain.model

import java.time.OffsetDateTime

/**
 * Modelo de domínio da nota.
 * Representa o conceito de negócio.
 */
data class Note(
    val id: Long?,
    val title: String,
    val content: String,
    val userId: Long,
    val lastUpdate: OffsetDateTime? = null
)
