package com.beatrizgnovais.repository

import com.beatrizgnovais.adapter.output.persistence.entity.Note
import com.beatrizgnovais.adapter.output.persistence.entity.User
import org.springframework.data.jpa.repository.JpaRepository


class NoteRepository {
    interface NoteRepository : JpaRepository<Note, Long> {
        fun findAllByUser(user: User): List<Note>
    }
}