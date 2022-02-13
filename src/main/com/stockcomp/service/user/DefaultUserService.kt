package com.stockcomp.service.user

import com.stockcomp.domain.user.Role
import com.stockcomp.dto.user.UserDetailsDto
import com.stockcomp.exception.DuplicateCredentialException
import com.stockcomp.repository.UserRepository
import com.stockcomp.request.AuthenticationRequest
import com.stockcomp.request.SignUpRequest
import com.stockcomp.util.toUserDetailsDto
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

    override fun loadUserByUsername(userName: String): UserDetails =
        userRepository.findByUsername(userName)
            .let { User(it.username, it.password, emptyList()) }


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

    override fun signInUser(request: AuthenticationRequest): String =
        userRepository.findByUsername(request.username).userRole.toString()


    override fun updateUserDetails(userDetailsDto: UserDetailsDto) {
        userRepository.findByUsername(userDetailsDto.username).let {
            it.country = userDetailsDto.country
            it.fullName = userDetailsDto.fullName
            userRepository.save(it)
        }
    }

    override fun getUserDetails(username: String): UserDetailsDto =
        userRepository.findByUsername(username).toUserDetailsDto()


    override fun verifyAdminUser(username: String): Boolean =
        userRepository.findByUsername(username).userRole == Role.ADMIN


    override fun findUserByUsername(username: String): com.stockcomp.domain.user.User? =
        userRepository.findByUsername(username)
}