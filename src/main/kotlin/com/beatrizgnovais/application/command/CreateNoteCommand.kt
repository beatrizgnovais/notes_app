package com.beatrizgnovais.application.command

data class CreateNoteCommand(
    val title: String,
    val content: String,
    val userId: Long
)