package com.beatrizgnovais.adapter.input.web.dto

/**
 * DTO recebido via HTTP.
 */
data class CreateNoteRequest(
    val title: String,
    val content: String
)