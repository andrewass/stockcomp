package com.stockcomp.user.internal

import com.stockcomp.user.UserStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserAdministrationService(
    private val userRepository: UserRepository,
    private val userCreationService: UserCreationService,
) {
    fun getAllUsersSortedByEmail(
        pageNumber: Int,
        pageSize: Int,
    ): Page<User> = userRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("email")))

    fun createUser(email: String): User = userCreationService.createUser(email)

    @Transactional
    fun updateUserStatus(
        userId: Long,
        newStatus: UserStatus,
    ): User {
        val user =
            userRepository
                .findById(userId)
                .orElseThrow { NoSuchElementException("User with id $userId not found") }
        user.updateStatus(newStatus)
        return userRepository.save(user)
    }
}
