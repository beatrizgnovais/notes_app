package com.beatrizgnovais.application.port.output

import com.beatrizgnovais.domain.model.Note

interface PdfGeneratorPort {
    fun generateNotePdf(note: Note): ByteArray
}
