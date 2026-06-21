package com.stockcomp.user.internal

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

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
}
