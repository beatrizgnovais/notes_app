package com.beatrizgnovais.application.service

import com.beatrizgnovais.application.command.CreateUserCommand
import com.beatrizgnovais.application.command.UpdateUserCommand
import com.beatrizgnovais.application.exception.ConflictException
import com.beatrizgnovais.application.exception.ResourceNotFoundException
import com.beatrizgnovais.application.port.input.UserUseCase
import com.beatrizgnovais.application.port.output.UserRepositoryPort
import com.beatrizgnovais.domain.model.User
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepositoryPort: UserRepositoryPort
) : UserUseCase {

    override fun create(command: CreateUserCommand): User {
        if (userRepositoryPort.existsByEmail(command.email)) {
            throw ConflictException("E-mail ja cadastrado.")
        }

        return userRepositoryPort.save(
            User(
                id = null,
                email = command.email,
                password = command.password
            )
        )
    }

    override fun list(): List<User> = userRepositoryPort.findAll()

    override fun getById(id: Long): User =
        userRepositoryPort.findById(id)
            ?: throw ResourceNotFoundException("Usuario com id=$id nao encontrado.")

    override fun update(id: Long, command: UpdateUserCommand): User {
        val existingUser = userRepositoryPort.findById(id)
            ?: throw ResourceNotFoundException("Usuario com id=$id nao encontrado.")

        val userWithSameEmail = userRepositoryPort.findByEmail(command.email)
        if (userWithSameEmail != null && userWithSameEmail.id != id) {
            throw ConflictException("E-mail ja cadastrado por outro usuario.")
        }

        val updatedUser = existingUser.copy(
            email = command.email,
            password = command.password
        )

        return userRepositoryPort.update(updatedUser)
    }

    override fun delete(id: Long) {
        if (userRepositoryPort.findById(id) == null) {
            throw ResourceNotFoundException("Usuario com id=$id nao encontrado.")
        }

        userRepositoryPort.deleteById(id)
    }
}
