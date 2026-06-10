package com.beatrizgnovais.adapter.output.pdf

import com.beatrizgnovais.application.command.ParsedPdfContent
import com.beatrizgnovais.application.exception.BadRequestException
import com.beatrizgnovais.application.port.output.PdfParserPort
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream

@Component
class PdfBoxParserAdapter : PdfParserPort {

    override fun parse(pdfBytes: ByteArray): ParsedPdfContent {
        val document = PDDocument.load(ByteArrayInputStream(pdfBytes))

        document.use { pdf ->
            val stripper = PDFTextStripper()
            val fullText = stripper.getText(pdf).trim()

            if (fullText.isBlank()) {
                throw BadRequestException("O PDF enviado nao contem texto legivel.")
            }

            val metadataTitle = pdf.documentInformation?.title?.takeIf { it.isNotBlank() }

            return if (metadataTitle != null) {
                ParsedPdfContent(
                    title = metadataTitle.take(120),
                    content = fullText
                )
            } else {
                val lines = fullText.lines().filter { it.isNotBlank() }
                val title = lines.first().trim().take(120)
                val content = lines.drop(1).joinToString("\n").trim().ifBlank { title }
                ParsedPdfContent(title = title, content = content)
            }
        }
    }
}
