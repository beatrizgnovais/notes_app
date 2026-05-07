package com.beatrizgnovais.adapter.input.web.dto

data class NoteResponse(
    val id: Long,
    val title: String,
    val content: String,
    val userId: Long,
    val lastUpdate: String?
)
