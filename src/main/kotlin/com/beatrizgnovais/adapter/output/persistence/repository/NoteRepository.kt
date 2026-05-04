package com.beatrizgnovais.adapter.output.persistence.repository

import com.beatrizgnovais.adapter.output.persistence.entity.NoteEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NoteRepository : JpaRepository<NoteEntity, Long>
