package com.beatrizgnovais.application.service

import com.beatrizgnovais.application.command.CreateNoteCommand
import com.beatrizgnovais.application.port.input.CreateNoteUseCase
import com.beatrizgnovais.application.port.output.NoteRepositoryPort
import com.beatrizgnovais.domain.model.Note
import org.springframework.stereotype.Service

/**
 * Implementação do caso de uso de criação de notas.
 */
@Service
class CreateNoteService(
    private val noteRepository: NoteRepositoryPort
) : CreateNoteUseCase {

    override fun createNote(command: CreateNoteCommand): Note {

        val note = Note(
            id = null,
            title = command.title,
            content = command.content,
            userId = command.userId
        )

        return noteRepository.save(note)
    }
}