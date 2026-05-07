package com.beatrizgnovais.domain.model

/**
 * Modelo de domínio que representa um usuário.
 * Não depende de banco, Spring ou frameworks.
 */
data class User(
    val id: Long?,
    val email: String,
    val password: String
)