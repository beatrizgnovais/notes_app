package com.beatrizgnovais.adapter.output.persistence.entity

import jakarta.persistence.*


@Entity
@Table(name = "notes")
data class NoteEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,

    val title: String,

    val content: String,

    val userId: Long
)