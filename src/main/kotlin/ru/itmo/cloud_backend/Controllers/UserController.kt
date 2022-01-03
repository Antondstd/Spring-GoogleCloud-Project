package ru.itmo.cloud_backend.Controllers


import com.google.api.client.http.AbstractInputStreamContent
import com.google.api.services.storage.model.StorageObject
import com.google.cloud.storage.Bucket
import com.google.cloud.storage.BucketInfo
import com.google.cloud.storage.Storage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.itmo.cloud_backend.Models.AuthenticationRequest
import ru.itmo.cloud_backend.Services.UserService
import java.io.IOException
import java.io.InputStream


@RestController

class UserController {
    @Autowired
    lateinit var userService: UserService

    @PostMapping("/user")
    fun adduser(
        @RequestBody  authenticationRequest: AuthenticationRequest
    ): ResponseEntity<String> {
        val token = userService.registerUser(authenticationRequest)
        if (token == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not able to register user")
        return ResponseEntity.ok(token)
    }

    @PostMapping("/authenticate")
    fun authentification(@RequestBody authenticationRequest: AuthenticationRequest): ResponseEntity<String> {

        val token = userService.authorization(authenticationRequest)
        if (token == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Login or Password")
        return ResponseEntity.ok(token)
    }

    @GetMapping("/profile")
    fun profile(): String {
        var auth = SecurityContextHolder.getContext().authentication
        return "Hello ${auth.name}"
    }

}
