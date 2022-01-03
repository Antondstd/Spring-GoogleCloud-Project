package ru.itmo.cloud_backend.Models

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "CL_USERS")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    var id: Long = 0,

    @Column(name = "email", nullable = false, unique = true)
    var email: String,
    @JsonIgnore
    @Column(name = "password", nullable = false)
    var password: String,

    @Column(name = "role", nullable = false)
    var role: UserRoles = UserRoles.ROLE_USER,

) : Serializable {
}

enum class UserRoles {
    ROLE_USER,
    ROLE_MODERATOR,
    ROLE_ADMIN
}
