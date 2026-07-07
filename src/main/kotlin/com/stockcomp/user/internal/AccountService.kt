package com.stockcomp.user.internal

import com.stockcomp.user.UpdateAccountSettingsRequest
import com.stockcomp.user.UserStatus
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AccountService(
    private val userRepository: UserRepository,
) {
    fun getAccount(userId: Long): User = findUserById(userId)

    @Transactional
    fun updateAccount(
        userId: Long,
        request: UpdateAccountSettingsRequest,
    ): User {
        if (userRepository.existsByUsernameAndUserIdNot(request.username, userId)) {
            throw UsernameAlreadyExistsException(request.username)
        }
        val user = findUserById(userId)
        user.updateUserDetails(
            username = request.username,
            fullName = request.fullName,
            country = request.country,
        )
        return try {
            userRepository.saveAndFlush(user)
        } catch (_: DataIntegrityViolationException) {
            throw UsernameAlreadyExistsException(request.username)
        }
    }

    fun isAdmin(userId: Long): Boolean = userId == 1L

    @Transactional
    fun updateAccountStatus(
        userId: Long,
        newStatus: UserStatus,
    ): User {
        val user = findUserById(userId)
        user.updateStatus(newStatus)
        return userRepository.save(user)
    }

    private fun findUserById(userId: Long): User =
        userRepository
            .findById(userId)
            .orElseThrow { NoSuchElementException("User with id $userId not found") }
}
