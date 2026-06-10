package com.beatrizgnovais.application.port.input

import com.beatrizgnovais.application.command.CreateNoteCommand
import com.beatrizgnovais.application.command.CreateNoteFromPdfCommand
import com.beatrizgnovais.application.command.UpdateNoteCommand
import com.beatrizgnovais.domain.model.Note

interface NoteUseCase {
    fun create(command: CreateNoteCommand): Note
    fun createFromPdf(command: CreateNoteFromPdfCommand): Note
    fun list(): List<Note>
    fun getById(id: Long): Note
    fun update(id: Long, command: UpdateNoteCommand): Note
    fun delete(id: Long)
}
