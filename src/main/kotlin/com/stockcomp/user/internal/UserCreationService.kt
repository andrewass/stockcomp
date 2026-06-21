package com.stockcomp.user.internal

import org.springframework.stereotype.Service

@Service
class UserCreationService(
    private val userRepository: UserRepository,
) {
    private val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')

    fun createUser(email: String): User {
        var username: String
        do {
            username = generateRandomUsername()
        } while (userRepository.existsByUsername(username))

        return userRepository.save(User(email = email, username = username))
    }

    private fun generateRandomUsername(): String =
        (1..15)
            .map { allowedChars.random() }
            .joinToString("")
}
