package com.beatrizgnovais.adapter.input.web.controller

import com.beatrizgnovais.adapter.input.web.dto.CreateNoteRequest
import com.beatrizgnovais.application.command.CreateNoteCommand
import com.beatrizgnovais.application.port.input.CreateNoteUseCase
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notes")
class NoteController(
    private val createNoteUseCase: CreateNoteUseCase
) {

    /**
     * Endpoint HTTP para criar nota.
     */
    @PostMapping
    fun createNote(@RequestBody request: CreateNoteRequest) {

        val command = CreateNoteCommand(
            title = request.title,
            content = request.content,
            userId = 1 // temporário
        )

        createNoteUseCase.createNote(command)
    }
}