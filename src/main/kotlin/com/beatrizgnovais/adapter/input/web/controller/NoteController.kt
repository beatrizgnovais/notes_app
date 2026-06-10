package com.beatrizgnovais.adapter.input.web.controller

import com.beatrizgnovais.adapter.input.web.dto.CreateNoteRequest
import com.beatrizgnovais.adapter.input.web.dto.NoteResponse
import com.beatrizgnovais.adapter.input.web.dto.UpdateNoteRequest
import com.beatrizgnovais.application.command.CreateNoteCommand
import com.beatrizgnovais.application.command.CreateNoteFromPdfCommand
import com.beatrizgnovais.application.command.UpdateNoteCommand
import com.beatrizgnovais.application.port.input.NoteUseCase
import com.beatrizgnovais.application.port.output.PdfGeneratorPort
import com.beatrizgnovais.domain.model.Note
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Tag(name = "Notas", description = "Operacoes de gerenciamento de notas")
@RestController
@RequestMapping("/notes")
class NoteController(
    private val noteUseCase: NoteUseCase,
    private val pdfGeneratorPort: PdfGeneratorPort
) {

    @Operation(summary = "Criar nota", description = "Cria uma nova nota vinculada a um usuario existente")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Nota criada com sucesso"),
        ApiResponse(responseCode = "400", description = "Dados invalidos"),
        ApiResponse(responseCode = "404", description = "Usuario nao encontrado")
    ])
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreateNoteRequest): NoteResponse {
        val created = noteUseCase.create(
            CreateNoteCommand(
                title = request.title,
                content = request.content,
                userId = request.userId
            )
        )
        return created.toResponse()
    }

    @Operation(summary = "Listar notas", description = "Retorna todas as notas cadastradas")
    @ApiResponse(responseCode = "200", description = "Lista de notas retornada com sucesso")
    @GetMapping
    fun list(): List<NoteResponse> = noteUseCase.list().map { it.toResponse() }

    @Operation(summary = "Buscar nota por ID", description = "Retorna uma nota especifica pelo seu ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Nota encontrada"),
        ApiResponse(responseCode = "404", description = "Nota nao encontrada")
    ])
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): NoteResponse = noteUseCase.getById(id).toResponse()

    @Operation(summary = "Atualizar nota", description = "Atualiza o titulo e conteudo de uma nota existente")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Nota atualizada com sucesso"),
        ApiResponse(responseCode = "400", description = "Dados invalidos"),
        ApiResponse(responseCode = "404", description = "Nota nao encontrada")
    ])
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateNoteRequest
    ): NoteResponse {
        val updated = noteUseCase.update(
            id = id,
            command = UpdateNoteCommand(
                title = request.title,
                content = request.content
            )
        )
        return updated.toResponse()
    }

    @Operation(summary = "Deletar nota", description = "Remove uma nota pelo seu ID")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Nota deletada com sucesso"),
        ApiResponse(responseCode = "404", description = "Nota nao encontrada")
    ])
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        noteUseCase.delete(id)
    }

    @Operation(
        summary = "Importar PDF como nota",
        description = "Recebe um arquivo PDF, extrai o texto e cria uma nova nota com o conteudo extraido"
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "Nota criada com sucesso a partir do PDF"),
        ApiResponse(responseCode = "400", description = "Arquivo invalido ou PDF sem texto legivel"),
        ApiResponse(responseCode = "404", description = "Usuario nao encontrado")
    ])
    @PostMapping("/from-pdf", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun createFromPdf(
        @RequestParam file: MultipartFile,
        @RequestParam userId: Long
    ): NoteResponse {
        val created = noteUseCase.createFromPdf(
            CreateNoteFromPdfCommand(
                pdfBytes = file.bytes,
                userId = userId
            )
        )
        return created.toResponse()
    }

    @Operation(
        summary = "Download PDF da nota",
        description = "Gera e retorna um arquivo PDF com o titulo, conteudo e data de atualizacao da nota"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "PDF gerado e retornado para download",
            content = [Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)]
        ),
        ApiResponse(responseCode = "404", description = "Nota nao encontrada")
    ])
    @GetMapping("/{id}/pdf")
    fun downloadPdf(@PathVariable id: Long): ResponseEntity<ByteArray> {
        val note = noteUseCase.getById(id)
        val pdfBytes = pdfGeneratorPort.generateNotePdf(note)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"nota-${id}.pdf\"")
            .header(HttpHeaders.CONTENT_LENGTH, pdfBytes.size.toString())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(pdfBytes)
    }

    private fun Note.toResponse(): NoteResponse = NoteResponse(
        id = requireNotNull(id) { "Nota retornada sem id." },
        title = title,
        content = content,
        userId = userId,
        lastUpdate = lastUpdate
            ?.withOffsetSameInstant(ZoneOffset.UTC)
            ?.format(LAST_UPDATE_FORMATTER)
    )

    companion object {
        private val LAST_UPDATE_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss 'UTC'")
    }
}