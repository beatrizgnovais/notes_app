package com.beatrizgnovais.application.command

data class UpdateUserCommand(
    val email: String,
    val password: String
)
