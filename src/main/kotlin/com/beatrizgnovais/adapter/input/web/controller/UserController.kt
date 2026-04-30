package com.beatrizgnovais.adapter.input.web.controller

import com.beatrizgnovais.adapter.input.web.dto.CreateUserRequest
import com.beatrizgnovais.adapter.input.web.dto.UpdateUserRequest
import com.beatrizgnovais.adapter.input.web.dto.UserResponse
import com.beatrizgnovais.application.command.CreateUserCommand
import com.beatrizgnovais.application.command.UpdateUserCommand
import com.beatrizgnovais.application.port.input.UserUseCase
import com.beatrizgnovais.domain.model.User
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userUseCase: UserUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreateUserRequest): UserResponse {
        val created = userUseCase.create(
            CreateUserCommand(
                email = request.email,
                password = request.password
            )
        )
        return created.toResponse()
    }

    @GetMapping
    fun list(): List<UserResponse> = userUseCase.list().map { it.toResponse() }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): UserResponse = userUseCase.getById(id).toResponse()

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateUserRequest
    ): UserResponse {
        val updated = userUseCase.update(
            id = id,
            command = UpdateUserCommand(
                email = request.email,
                password = request.password
            )
        )
        return updated.toResponse()
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        userUseCase.delete(id)
    }

    private fun User.toResponse(): UserResponse = UserResponse(
        id = requireNotNull(id) { "Usuario retornado sem id." },
        email = email
    )
}
