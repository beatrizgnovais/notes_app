package com.beatrizgnovais.adapter.input.web.exception

data class ApiValidationErrorResponse(
    val message: String,
    val errors: Map<String, String>
)
