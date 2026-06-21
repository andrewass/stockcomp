package com.stockcomp.user.internal

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserIdentityService(
    private val userRepository: UserRepository,
    private val userCreationService: UserCreationService,
) {
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
                userCreationService.createUser(userSubject)
            } catch (_: DataIntegrityViolationException) {
                userRepository.findByEmail(userSubject)
                    ?: throw IllegalStateException("Unable to resolve user for subject $userSubject after constraint violation")
            }
        return ensureUserSubjectMapping(createdUser, userSubject)
    }

    fun findUserByUsername(username: String): User =
        userRepository.findByUsername(username)
            ?: throw NoSuchElementException("User with username $username not found")

    fun findUsersById(userIds: List<Long>): List<User> = userRepository.findAllById(userIds)

    fun findUserById(userId: Long): User =
        userRepository
            .findById(userId)
            .orElseThrow { NoSuchElementException("User with id $userId not found") }

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
}
