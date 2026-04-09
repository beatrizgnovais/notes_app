package com.beatrizgnovais.adapter.output.persistence.entity // O pacote deve seguir a pasta

import jakarta.persistence.*
import java.util.UUID // IMPORTANTE: Use o java.util e não o do hibernate validator

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // AUTO funciona melhor com UUID no Postgres
    val id: UUID? = null,

    @Column(unique = true, nullable = false)
    var email: String = "",

    @Column(nullable = false)
    var password: String = ""
)