package com.beatrizgnovais.application.port.output

import com.beatrizgnovais.domain.model.User

/**
 * Porta de saída para persistência de usuários.
 */
interface UserRepositoryPort {

    fun save(user: User): User

    fun findByEmail(email: String): User?
}