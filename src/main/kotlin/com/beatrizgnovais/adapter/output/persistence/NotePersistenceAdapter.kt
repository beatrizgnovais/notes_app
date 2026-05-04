package com.beatrizgnovais.adapter.output.persistence

import com.beatrizgnovais.adapter.output.persistence.entity.NoteEntity
import com.beatrizgnovais.application.port.output.NoteRepositoryPort
import com.beatrizgnovais.domain.model.Note
import com.beatrizgnovais.adapter.output.persistence.repository.NoteRepository
import org.springframework.stereotype.Repository

@Repository
class NotePersistenceAdapter(
    private val noteRepository: NoteRepository
) : NoteRepositoryPort {

    override fun save(note: Note): Note = noteRepository.save(note.toEntity()).toDomain()

    override fun findAll(): List<Note> = noteRepository.findAll().map { it.toDomain() }

    override fun findById(id: Long): Note? = noteRepository.findById(id).orElse(null)?.toDomain()

    override fun update(note: Note): Note = noteRepository.save(note.toEntity()).toDomain()

    override fun deleteById(noteId: Long) = noteRepository.deleteById(noteId)

    private fun Note.toEntity(): NoteEntity = NoteEntity(
        id = id,
        title = title,
        content = content,
        userId = userId
    )

    private fun NoteEntity.toDomain(): Note = Note(
        id = id,
        title = title,
        content = content,
        userId = userId
    )
}
