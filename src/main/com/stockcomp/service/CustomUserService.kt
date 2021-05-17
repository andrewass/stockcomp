package com.stockcomp.service

import com.stockcomp.exception.DuplicateCredentialException
import com.stockcomp.repository.jpa.UserRepository
import com.stockcomp.request.SignUpRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class CustomUserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    override fun loadUserByUsername(userName: String): UserDetails {
        val persistedUser = userRepository.findByUsername(userName).get()

        return User(persistedUser.username, persistedUser.password, emptyList())
    }

    fun addNewUser(request: SignUpRequest): com.stockcomp.entity.User {
        if (userRepository.existsByUsername(request.username)) {
            throw DuplicateCredentialException("Existing username : ${request.username}")
        }
        val user = com.stockcomp.entity.User(
            username = request.username,
            password = passwordEncoder.encode(request.password),
            email = request.email
        )
        return userRepository.save(user)
    }

    fun getPersistedUser(username: String): com.stockcomp.entity.User {
        return userRepository.findByUsername(username).get()
    }
}