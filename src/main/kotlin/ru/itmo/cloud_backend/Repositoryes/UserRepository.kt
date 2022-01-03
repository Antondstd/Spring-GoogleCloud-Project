package ru.itmo.cloud_backend.Repositoryes


import org.springframework.data.jpa.repository.JpaRepository
import ru.itmo.cloud_backend.Models.User
import java.util.*

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
}
