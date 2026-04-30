package com.beatrizgnovais.application.port.output

import com.beatrizgnovais.domain.model.User

/**
 * Porta de saída para persistência de usuários.
 */
interface UserRepositoryPort {
    fun save(user: User): User
    fun findAll(): List<User>
    fun findById(id: Long): User?
    fun findByEmail(email: String): User?
    fun update(user: User): User
    fun deleteById(id: Long)
    fun existsByEmail(email: String): Boolean
}