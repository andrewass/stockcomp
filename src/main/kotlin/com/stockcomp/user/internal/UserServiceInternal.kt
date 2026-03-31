package com.stockcomp.user.internal

import com.stockcomp.user.UserDetailsDto
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceInternal(
    private val userRepository: UserRepository,
) {
    private val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')

    fun getAllUsersSortedByEmail(
        pageNumber: Int,
        pageSize: Int,
    ): Page<User> = userRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("email")))

    @Transactional
    fun findOrCreateUserBySubject(userSubject: String): User {
        val existingBySubject = userRepository.findByUserSubject(userSubject)
        if (existingBySubject != null) {
            return existingBySubject
        }

        val existingByEmail = userRepository.findByEmail(userSubject)
        if (existingByEmail != null) {
            return ensureUserSubjectMapping(existingByEmail, userSubject)
        }

        val createdUser =
            try {
                createUser(userSubject)
            } catch (_: DataIntegrityViolationException) {
                userRepository.findByEmail(userSubject)
                    ?: throw IllegalStateException("Unable to resolve user for subject $userSubject after constraint violation")
            }
        return ensureUserSubjectMapping(createdUser, userSubject)
    }

    fun findUserByUsername(username: String): User = userRepository.findByUsername(username)

    fun findUsersById(userIds: List<Long>): List<User> = userRepository.findAllById(userIds)

    fun findUserById(userId: Long): User =
        userRepository
            .findById(userId)
            .orElseThrow { IllegalArgumentException("User with id $userId not found") }

    fun updateUser(
        userId: Long,
        userDetailsDto: UserDetailsDto,
    ) {
        val user = userRepository.getReferenceById(userId)
        user.updateUserDetails(
            username = userDetailsDto.username,
            fullName = userDetailsDto.fullName,
            country = userDetailsDto.country,
        )
        userRepository.save(user)
    }

    fun createUser(email: String): User {
        var username: String
        do {
            username = generateRandomUsername()
        } while (userRepository.existsByUsername(username))

        return User(email = email, username = username)
            .let { userRepository.save(it) }
    }

    fun isAdmin(userId: Long): Boolean = userId == 1L

    private fun ensureUserSubjectMapping(
        user: User,
        userSubject: String,
    ): User {
        val alreadyMapped =
            user.userSubjects.any {
                it.externalSubjectId == userSubject && it.subjectProvider == SubjectProvider.GOOGLE
            }
        if (alreadyMapped) {
            return user
        }

        user.addUserSubject(
            UserSubject(
                subjectProvider = SubjectProvider.GOOGLE,
                user = user,
                isValid = true,
                externalSubjectId = userSubject,
            ),
        )
        return userRepository.save(user)
    }

    private fun generateRandomUsername(): String =
        (1..15)
            .map { allowedChars.random() }
            .joinToString("")
}
