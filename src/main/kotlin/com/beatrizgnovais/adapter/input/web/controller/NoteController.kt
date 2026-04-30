package com.beatrizgnovais.adapter.input.web.controller

import com.beatrizgnovais.adapter.input.web.dto.CreateNoteRequest
import com.beatrizgnovais.adapter.input.web.dto.NoteResponse
import com.beatrizgnovais.adapter.input.web.dto.UpdateNoteRequest
import com.beatrizgnovais.application.command.CreateNoteCommand
import com.beatrizgnovais.application.command.UpdateNoteCommand
import com.beatrizgnovais.application.port.input.NoteUseCase
import com.beatrizgnovais.domain.model.Note
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notes")
class NoteController(
    private val noteUseCase: NoteUseCase
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreateNoteRequest): NoteResponse {
        val created = noteUseCase.create(
            CreateNoteCommand(
                title = request.title,
                content = request.content,
                userId = request.userId
            )
        )
        return created.toResponse()
    }

    @GetMapping
    fun list(): List<NoteResponse> = noteUseCase.list().map { it.toResponse() }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): NoteResponse = noteUseCase.getById(id).toResponse()

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateNoteRequest
    ): NoteResponse {
        val updated = noteUseCase.update(
            id = id,
            command = UpdateNoteCommand(
                title = request.title,
                content = request.content
            )
        )
        return updated.toResponse()
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        noteUseCase.delete(id)
    }

    private fun Note.toResponse(): NoteResponse = NoteResponse(
        id = requireNotNull(id) { "Nota retornada sem id." },
        title = title,
        content = content,
        userId = userId
    )
}