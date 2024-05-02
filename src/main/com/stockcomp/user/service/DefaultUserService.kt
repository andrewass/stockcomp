package com.stockcomp.user.service

import com.stockcomp.user.controller.mapToUserDetailsDto
import com.stockcomp.user.dto.UserDetailsDto
import com.stockcomp.user.entity.User
import com.stockcomp.user.entity.UserRole
import com.stockcomp.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class DefaultUserService @Autowired constructor(
    private val userRepository: UserRepository
) : UserService {

    private val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')

    override fun getAllUsersSortedByEmail(pageNumber: Int, pageSize: Int): Page<User> =
        userRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("email")))

    override fun findUserByTokenClaim(email: String): User =
        userRepository.findByEmail(email)
            ?: createUser(email)

    override fun findUserByEmail(email: String): User? =
        userRepository.findByEmail(email)
            ?: createUser(email)

    override fun findUserByUsername(username: String): User =
        userRepository.findByUsername(username)

    override fun updateUser(user: User, userDetailsDto: UserDetailsDto) {
        user.also {
            it.country = userDetailsDto.country ?: it.country
            it.username = userDetailsDto.username ?: it.username
            it.fullName = userDetailsDto.fullName ?: it.fullName
            userRepository.save(it)
        }
    }

    override fun getUserDetails(username: String): UserDetailsDto =
        mapToUserDetailsDto(userRepository.findByUsername(username))

    override fun verifyAdminUser(username: String): Boolean =
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