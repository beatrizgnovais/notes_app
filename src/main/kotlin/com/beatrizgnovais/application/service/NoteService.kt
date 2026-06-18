package com.beatrizgnovais.application.service

import com.beatrizgnovais.application.command.CreateNoteCommand
import com.beatrizgnovais.application.command.CreateNoteFromPdfCommand
import com.beatrizgnovais.application.command.UpdateNoteCommand
import com.beatrizgnovais.application.exception.ResourceNotFoundException
import com.beatrizgnovais.application.port.input.NoteUseCase
import com.beatrizgnovais.application.port.output.NoteRepositoryPort
import com.beatrizgnovais.application.port.output.PdfCachePort
import com.beatrizgnovais.application.port.output.PdfParserPort
import com.beatrizgnovais.application.port.output.UserRepositoryPort
import com.beatrizgnovais.domain.model.Note
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service

@Service
class NoteService(
    private val noteRepositoryPort: NoteRepositoryPort,
    private val userRepositoryPort: UserRepositoryPort,
    private val pdfParserPort: PdfParserPort,
    private val pdfCachePort: PdfCachePort
) : NoteUseCase {

    /**
     * Cria nota e invalida o cache da lista, pois um novo item foi adicionado.
     */
    @CacheEvict(value = ["notes-list"], allEntries = true)
    override fun create(command: CreateNoteCommand): Note {
        ensureUserExists(command.userId)

        return noteRepositoryPort.save(
            Note(
                id = null,
                title = command.title,
                content = command.content,
                userId = command.userId
            )
        )
    }

    /**
     * Cria nota a partir de PDF e invalida o cache da lista.
     */
    @CacheEvict(value = ["notes-list"], allEntries = true)
    override fun createFromPdf(command: CreateNoteFromPdfCommand): Note {
        ensureUserExists(command.userId)

        val parsed = pdfParserPort.parse(command.pdfBytes)

        return noteRepositoryPort.save(
            Note(
                id = null,
                title = parsed.title,
                content = parsed.content,
                userId = command.userId
            )
        )
    }

    /**
     * Lista todas as notas.
     * O resultado inteiro e cacheado com TTL de 5 minutos (configurado em CacheConfig).
     */
    @Cacheable(value = ["notes-list"])
    override fun list(): List<Note> = noteRepositoryPort.findAll()

    /**
     * Busca nota por ID.
     * Cache HIT: retorna direto do Redis sem tocar o PostgreSQL.
     * Cache MISS: busca no BD e armazena no Redis com TTL de 10 minutos.
     */
    @Cacheable(value = ["notes"], key = "#id")
    override fun getById(id: Long): Note =
        noteRepositoryPort.findById(id)
            ?: throw ResourceNotFoundException("Nota com id=$id nao encontrada.")

    /**
     * Atualiza nota:
     *   - @CachePut atualiza o cache individual da nota com o novo valor
     *   - @CacheEvict invalida a lista (que ficou desatualizada)
     *   - evict manual do PDF, pois o conteudo mudou e o PDF gerado anteriormente esta obsoleto
     */
    @Caching(
        put = [CachePut(value = ["notes"], key = "#id")],
        evict = [CacheEvict(value = ["notes-list"], allEntries = true)]
    )
    override fun update(id: Long, command: UpdateNoteCommand): Note {
        val existingNote = noteRepositoryPort.findById(id)
            ?: throw ResourceNotFoundException("Nota com id=$id nao encontrada.")

        pdfCachePort.evict(id)

        return noteRepositoryPort.update(
            existingNote.copy(
                title = command.title,
                content = command.content
            )
        )
    }

    /**
     * Deleta nota:
     *   - @Caching invalida o cache individual e a lista
     *   - evict manual do PDF gerado para essa nota
     */
    @Caching(evict = [
        CacheEvict(value = ["notes"], key = "#id"),
        CacheEvict(value = ["notes-list"], allEntries = true)
    ])
    override fun delete(id: Long) {
        if (noteRepositoryPort.findById(id) == null) {
            throw ResourceNotFoundException("Nota com id=$id nao encontrada.")
        }

        pdfCachePort.evict(id)
        noteRepositoryPort.deleteById(id)
    }

    private fun ensureUserExists(userId: Long) {
        if (userRepositoryPort.findById(userId) == null) {
            throw ResourceNotFoundException("Usuario com id=$userId nao encontrado para associar nota.")
        }
    }
}
