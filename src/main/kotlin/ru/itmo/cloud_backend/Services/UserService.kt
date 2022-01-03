package ru.itmo.cloud_backend.Services


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import ru.itmo.cloud_backend.Models.AuthenticationRequest
import ru.itmo.cloud_backend.Models.MyUserDetails
import ru.itmo.cloud_backend.Models.User
import ru.itmo.cloud_backend.Repositoryes.UserRepository
import ru.itmo.cloud_backend.Utils.JwtUtil

@Service
@Primary
class UserService(
    private val userRepository: UserRepository
) : UserRepository by userRepository {

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var jwtUtil: JwtUtil

    fun getCurrentUser(): User {
        return findByEmail((SecurityContextHolder.getContext().authentication.principal as MyUserDetails).username)!!
    }

    fun registerUser(authenticationRequest: AuthenticationRequest): String? {
        try {
            if (authenticationRequest.username == null || authenticationRequest.password == null)
                return null
            val user = User(
                email = authenticationRequest.username!!,
                password = authenticationRequest.password!!
            )
            save(user)
            return authorization(authenticationRequest)
        } catch (e: Exception) {
            return null
        }
    }

    fun authorization(authenticationRequest: AuthenticationRequest): String? {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    authenticationRequest.username,
                    authenticationRequest.password
                )
            )
        } catch (e: Exception) {
            return null
        }
        val user = findByEmail(authenticationRequest.username!!)
        if (user != null) {
            val jwtToken = jwtUtil.generateToken(user.email)
            return jwtUtil.generateToken(user.email)
        }
        return null
    }
}
