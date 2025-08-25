package com.stockcomp.user.internal

import com.stockcomp.user.UserDetailsDto
import com.stockcomp.user.toUserDetailsDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class UserServiceInternal(
    private val userRepository: UserRepository
) {

    private val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')

    fun getAllUsersSortedByEmail(pageNumber: Int, pageSize: Int): Page<User> =
        userRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("email")))

    fun findOrCreateUserByEmail(email: String): User = userRepository.findByEmail(email) ?: createUser(email)

    fun findUserByEmail(email: String): User =
        userRepository.findByEmail(email) ?: throw IllegalStateException("User with email $email not found")

    fun findUserByUsername(username: String): User =
        userRepository.findByUsername(username)

    fun findUserById(userId: Long): User =
        userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User with id $userId not found") }

    fun updateUser(userId: Long, userDetailsDto: UserDetailsDto) {
        userRepository.getReferenceById(userId).apply {
            country = userDetailsDto.country
            username = userDetailsDto.username
            fullName = userDetailsDto.fullName
        }.also { userRepository.save(it) }
    }

    fun getUserDetails(username: String): UserDetailsDto =
        toUserDetailsDto(userRepository.findByUsername(username))

    fun verifyAdminUser(username: String): Boolean =
        userRepository.findByUsername(username).userRole == UserRole.ADMIN

    private fun createUser(email: String): User {
        var username: String
        do {
            username = generateRandomUsername()
        } while (userRepository.existsByUsername(username))

        return User(email = email, username = username)
            .let { userRepository.save(it) }
    }

    private fun generateRandomUsername(): String =
        (1..15).map { allowedChars.random() }
            .joinToString("")
}
