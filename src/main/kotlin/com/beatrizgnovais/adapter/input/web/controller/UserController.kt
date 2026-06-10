package com.beatrizgnovais.adapter.input.web.controller

import com.beatrizgnovais.adapter.input.web.dto.CreateUserRequest
import com.beatrizgnovais.adapter.input.web.dto.UpdateUserRequest
import com.beatrizgnovais.adapter.input.web.dto.UserResponse
import com.beatrizgnovais.application.command.CreateUserCommand
import com.beatrizgnovais.application.command.UpdateUserCommand
import com.beatrizgnovais.application.port.input.UserUseCase
import com.beatrizgnovais.domain.model.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(name = "Usuarios", description = "Operacoes de gerenciamento de usuarios")
@RestController
@RequestMapping("/users")
class UserController(
    private val userUseCase: UserUseCase
) {

    @Operation(summary = "Criar usuario", description = "Cadastra um novo usuario com e-mail unico")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Usuario criado com sucesso"),
        ApiResponse(responseCode = "400", description = "Dados invalidos"),
        ApiResponse(responseCode = "409", description = "E-mail ja cadastrado")
    ])
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

    @Operation(summary = "Listar usuarios", description = "Retorna todos os usuarios cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios retornada com sucesso")
    @GetMapping
    fun list(): List<UserResponse> = userUseCase.list().map { it.toResponse() }

    @Operation(summary = "Buscar usuario por ID", description = "Retorna um usuario especifico pelo seu ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        ApiResponse(responseCode = "404", description = "Usuario nao encontrado")
    ])
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): UserResponse = userUseCase.getById(id).toResponse()

    @Operation(summary = "Atualizar usuario", description = "Atualiza o e-mail e senha de um usuario existente")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Usuario atualizado com sucesso"),
        ApiResponse(responseCode = "400", description = "Dados invalidos"),
        ApiResponse(responseCode = "404", description = "Usuario nao encontrado"),
        ApiResponse(responseCode = "409", description = "E-mail ja em uso por outro usuario")
    ])
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

    @Operation(summary = "Deletar usuario", description = "Remove um usuario pelo seu ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Usuario deletado com sucesso"),
        ApiResponse(responseCode = "404", description = "Usuario nao encontrado")
    ])
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
