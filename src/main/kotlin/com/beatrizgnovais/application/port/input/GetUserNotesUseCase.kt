package com.beatrizgnovais.application.port.input

import com.beatrizgnovais.domain.model.Note

interface GetUserNotesUseCase {

    fun getNotes(userId: Long): List<Note>
}