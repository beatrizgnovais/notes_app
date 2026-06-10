package com.beatrizgnovais.application.port.output

import com.beatrizgnovais.application.command.ParsedPdfContent

interface PdfParserPort {
    fun parse(pdfBytes: ByteArray): ParsedPdfContent
}
