package com.beatrizgnovais.adapter.input.web.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateUserRequest(
    @field:Email(message = "E-mail invalido.")
    @field:NotBlank(message = "E-mail eh obrigatorio.")
    val email: String,

    @field:NotBlank(message = "Senha eh obrigatoria.")
    @field:Size(min = 6, message = "Senha deve ter ao menos 6 caracteres.")
    val password: String
)
