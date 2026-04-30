package com.beatrizgnovais.adapter.input.web.exception

import com.beatrizgnovais.application.exception.ConflictException
import com.beatrizgnovais.application.exception.ResourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(ex: ResourceNotFoundException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiErrorResponse(message = ex.message ?: "Recurso nao encontrado."))

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(ex: ConflictException): ResponseEntity<ApiErrorResponse> =
        ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiErrorResponse(message = ex.message ?: "Conflito de dados."))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiValidationErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Valor invalido.") }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiValidationErrorResponse(
                    message = "Erro de validacao.",
                    errors = errors
                )
            )
    }
}
