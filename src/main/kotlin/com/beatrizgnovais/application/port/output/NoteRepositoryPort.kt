package com.beatrizgnovais.application.port.output

import com.beatrizgnovais.domain.model.Note

/**
 * Porta de saída para persistência de notas.
 */
interface NoteRepositoryPort {

    fun save(note: Note): Note

    fun findByUserId(userId: Long): List<Note>

    fun deleteById(noteId: Long)
}