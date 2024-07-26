package com.stockcomp.user.internal

import com.stockcomp.user.UserDetailsDto
import com.stockcomp.user.mapToUserDetailsDto
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

    fun findUserByTokenClaim(email: String): User =
        userRepository.findByEmail(email)
            ?: createUser(email)

    fun findUserByEmail(email: String): User? =
        userRepository.findByEmail(email)
            ?: createUser(email)

    fun findUserByUsername(username: String): User =
        userRepository.findByUsername(username)

    fun findUserById(userId: Long): User =
        userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User with id $userId not found") }

    fun updateUser(user: User, userDetailsDto: UserDetailsDto) {
        user.also {
            it.country = userDetailsDto.country ?: it.country
            it.username = userDetailsDto.username ?: it.username
            it.fullName = userDetailsDto.fullName ?: it.fullName
            userRepository.save(it)
        }
    }

    fun getUserDetails(username: String): UserDetailsDto =
        mapToUserDetailsDto(userRepository.findByUsername(username))

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