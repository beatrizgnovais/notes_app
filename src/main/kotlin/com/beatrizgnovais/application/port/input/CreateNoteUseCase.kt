package com.beatrizgnovais.application.port.input

import com.beatrizgnovais.application.command.CreateNoteCommand
import com.beatrizgnovais.domain.model.Note

/**
 * Define a funcionalidade de criar notas.
 */
interface CreateNoteUseCase {

    fun createNote(command: CreateNoteCommand): Note
}