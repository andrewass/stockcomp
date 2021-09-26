package com.stockcomp.service.user

import com.stockcomp.exception.DuplicateCredentialException
import com.stockcomp.repository.UserRepository
import com.stockcomp.request.AuthenticationRequest
import com.stockcomp.request.SignUpRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class DefaultUserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService, UserService {

    override fun loadUserByUsername(userName: String): UserDetails {
        val persistedUser = userRepository.findByUsername(userName)

        return User(persistedUser.username, persistedUser.password, emptyList())
    }

    override fun signUpUser(request: SignUpRequest): com.stockcomp.domain.user.User {
        if (userRepository.existsByUsername(request.username)) {
            throw DuplicateCredentialException("Existing username : ${request.username}")
        }
        val user = com.stockcomp.domain.user.User(
            username = request.username,
            password = passwordEncoder.encode(request.password),
            email = request.email,
            userRole = request.role
        )
        return userRepository.save(user)
    }

    override fun signInUser(request: AuthenticationRequest): String {
        val user = userRepository.findByUsername(request.username)

        return user.userRole.toString()
    }

    override fun findUserByUsername(username: String): com.stockcomp.domain.user.User? {
        return userRepository.findByUsername(username)
    }
}