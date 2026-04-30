package com.beatrizgnovais.application.port.input

import com.beatrizgnovais.application.command.CreateUserCommand
import com.beatrizgnovais.application.command.UpdateUserCommand
import com.beatrizgnovais.domain.model.User

interface UserUseCase {
    fun create(command: CreateUserCommand): User
    fun list(): List<User>
    fun getById(id: Long): User
    fun update(id: Long, command: UpdateUserCommand): User
    fun delete(id: Long)
}
