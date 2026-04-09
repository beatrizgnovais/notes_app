package com.beatrizgnovais.repository

import com.beatrizgnovais.adapter.output.persistence.entity.User
import org.springframework.data.jpa.repository.JpaRepository

class UserRepository {
    interface UserRepository : JpaRepository<User, Long> {
        fun findByEmail(email: String): User?
    }
}