package com.beatrizgnovais.application.command

data class CreateNoteFromPdfCommand(
    val pdfBytes: ByteArray,
    val userId: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CreateNoteFromPdfCommand) return false
        return userId == other.userId && pdfBytes.contentEquals(other.pdfBytes)
    }

    override fun hashCode(): Int {
        var result = pdfBytes.contentHashCode()
        result = 31 * result + userId.hashCode()
        return result
    }
}
