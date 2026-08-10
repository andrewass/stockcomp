package com.stockcomp.user.internal

import com.stockcomp.user.ExternalIdentity
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserIdentityService(
    private val userRepository: UserRepository,
    private val userCreationService: UserCreationService,
) {
    @Transactional
    fun findOrCreateUserByExternalIdentity(externalIdentity: ExternalIdentity): User {
        userRepository
            .findByExternalIdentity(externalIdentity.provider, externalIdentity.externalSubjectId)
            ?.let { return it }

        val verifiedEmail =
            requireNotNull(externalIdentity.verifiedEmail) {
                "A verified email claim is required when creating or linking an account"
            }
        // Supports the staged migration of existing accounts whose only external mapping is their email.
        val existingByEmail = userRepository.findByEmail(verifiedEmail)
        if (existingByEmail != null) {
            return ensureUserSubjectMapping(existingByEmail, externalIdentity)
        }

        val createdUser =
            try {
                userCreationService.createUser(verifiedEmail)
            } catch (_: DataIntegrityViolationException) {
                userRepository.findByEmail(verifiedEmail)
                    ?: throw IllegalStateException("Unable to resolve user for verified email $verifiedEmail after constraint violation")
            }
        return ensureUserSubjectMapping(createdUser, externalIdentity)
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
        externalIdentity: ExternalIdentity,
    ): User {
        val alreadyMapped =
            user.userSubjects.any {
                it.externalSubjectId == externalIdentity.externalSubjectId && it.subjectProvider == externalIdentity.provider
            }
        if (alreadyMapped) {
            return user
        }

        user.addUserSubject(
            UserSubject(
                subjectProvider = externalIdentity.provider,
                user = user,
                isValid = true,
                externalSubjectId = externalIdentity.externalSubjectId,
            ),
        )
        return userRepository.save(user)
    }
}
