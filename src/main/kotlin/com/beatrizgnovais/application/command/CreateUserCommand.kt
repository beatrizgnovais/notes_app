package com.beatrizgnovais.application.command

/**
 * Command representa uma ação do sistema.
 */
data class CreateUserCommand(
    val email: String,
    val password: String
)