package com.beatrizgnovais.adapter.output.pdf

import com.beatrizgnovais.application.port.output.PdfGeneratorPort
import com.beatrizgnovais.domain.model.Note
import org.openpdf.text.Document
import org.openpdf.text.Font
import org.openpdf.text.FontFactory
import org.openpdf.text.PageSize
import org.openpdf.text.Paragraph
import org.openpdf.text.pdf.PdfWriter
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Component
class OpenPdfGeneratorAdapter : PdfGeneratorPort {

    override fun generateNotePdf(note: Note): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val document = Document(PageSize.A4)

        PdfWriter.getInstance(document, outputStream)
        document.open()

        val titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18f)
        val bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12f)
        val metaFont = FontFactory.getFont(FontFactory.HELVETICA, 10f, Font.ITALIC)

        document.add(Paragraph(note.title, titleFont))
        document.add(Paragraph(" "))
        document.add(Paragraph(note.content, bodyFont))
        document.add(Paragraph(" "))

        val lastUpdateText = note.lastUpdate
            ?.withOffsetSameInstant(ZoneOffset.UTC)
            ?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss 'UTC'"))
            ?: "Data nao disponivel"

        document.add(Paragraph("Ultima atualizacao: $lastUpdateText", metaFont))

        document.close()

        return outputStream.toByteArray()
    }
}
