package com.beatrizgnovais.adapter.input.web.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateNoteRequest(
    @field:NotBlank(message = "Titulo é obrigatorio.")
    @field:Size(max = 120, message = "Titulo deve ter no maximo 120 caracteres.")
    val title: String,

    @field:NotBlank(message = "Conteudo é obrigatorio.")
    val content: String,

    @field:NotNull(message = "userId é obrigatorio.")
    val userId: Long
)
