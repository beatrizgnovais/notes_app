package com.beatrizgnovais.application.port.output

import com.beatrizgnovais.domain.model.Note

/**
 * Porta de saída para persistência de notas.
 */
interface NoteRepositoryPort {

    fun save(note: Note): Note

    fun findAll(): List<Note>

    fun findById(id: Long): Note?

    fun update(note: Note): Note

    fun deleteById(noteId: Long)
}