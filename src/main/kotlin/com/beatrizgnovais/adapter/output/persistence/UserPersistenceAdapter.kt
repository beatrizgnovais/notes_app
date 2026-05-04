package com.beatrizgnovais.adapter.output.persistence

import com.beatrizgnovais.adapter.output.persistence.entity.UserEntity
import com.beatrizgnovais.application.port.output.UserRepositoryPort
import com.beatrizgnovais.domain.model.User
import com.beatrizgnovais.adapter.output.persistence.repository.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserPersistenceAdapter(
    private val userRepository: UserRepository
) : UserRepositoryPort {

    override fun save(user: User): User = userRepository.save(user.toEntity()).toDomain()

    override fun findAll(): List<User> = userRepository.findAll().map { it.toDomain() }

    override fun findById(id: Long): User? = userRepository.findById(id).orElse(null)?.toDomain()

    override fun findByEmail(email: String): User? = userRepository.findByEmail(email)?.toDomain()

    override fun update(user: User): User = userRepository.save(user.toEntity()).toDomain()

    override fun deleteById(id: Long) = userRepository.deleteById(id)

    override fun existsByEmail(email: String): Boolean = userRepository.existsByEmail(email)

    private fun User.toEntity(): UserEntity = UserEntity(
        id = id,
        email = email,
        password = password
    )

    private fun UserEntity.toDomain(): User = User(
        id = id,
        email = email,
        password = password
    )
}
