package com.beatrizgnovais.application.service

import com.beatrizgnovais.application.command.CreateNoteCommand
import com.beatrizgnovais.application.command.CreateNoteFromPdfCommand
import com.beatrizgnovais.application.command.UpdateNoteCommand
import com.beatrizgnovais.application.exception.ResourceNotFoundException
import com.beatrizgnovais.application.port.input.NoteUseCase
import com.beatrizgnovais.application.port.output.NoteRepositoryPort
import com.beatrizgnovais.application.port.output.PdfParserPort
import com.beatrizgnovais.application.port.output.UserRepositoryPort
import com.beatrizgnovais.domain.model.Note
import org.springframework.stereotype.Service

@Service
class NoteService(
    private val noteRepositoryPort: NoteRepositoryPort,
    private val userRepositoryPort: UserRepositoryPort,
    private val pdfParserPort: PdfParserPort
) : NoteUseCase {

    override fun create(command: CreateNoteCommand): Note {
        ensureUserExists(command.userId)

        return noteRepositoryPort.save(
            Note(
                id = null,
                title = command.title,
                content = command.content,
                userId = command.userId
            )
        )
    }

    override fun createFromPdf(command: CreateNoteFromPdfCommand): Note {
        ensureUserExists(command.userId)

        val parsed = pdfParserPort.parse(command.pdfBytes)

        return noteRepositoryPort.save(
            Note(
                id = null,
                title = parsed.title,
                content = parsed.content,
                userId = command.userId
            )
        )
    }

    override fun list(): List<Note> = noteRepositoryPort.findAll()

    override fun getById(id: Long): Note =
        noteRepositoryPort.findById(id)
            ?: throw ResourceNotFoundException("Nota com id=$id nao encontrada.")

    override fun update(id: Long, command: UpdateNoteCommand): Note {
        val existingNote = noteRepositoryPort.findById(id)
            ?: throw ResourceNotFoundException("Nota com id=$id nao encontrada.")

        return noteRepositoryPort.update(
            existingNote.copy(
                title = command.title,
                content = command.content
            )
        )
    }

    override fun delete(id: Long) {
        if (noteRepositoryPort.findById(id) == null) {
            throw ResourceNotFoundException("Nota com id=$id nao encontrada.")
        }

        noteRepositoryPort.deleteById(id)
    }

    private fun ensureUserExists(userId: Long) {
        if (userRepositoryPort.findById(userId) == null) {
            throw ResourceNotFoundException("Usuario com id=$userId nao encontrado para associar nota.")
        }
    }
}
