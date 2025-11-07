package com.stockcomp.user

import com.stockcomp.user.internal.UserRole
import com.stockcomp.user.internal.UserServiceInternal
import org.springframework.stereotype.Service

@Service
class UserServiceExternal(
    private val userService: UserServiceInternal,
) {
    fun getUserIdByEmail(email: String): Long =
        userService.findOrCreateUserByEmail(email).userId
            ?: throw IllegalArgumentException("No user found for email $email")

    fun getUserIdByUsername(username: String): Long =
        userService.findUserByUsername(username).userId
            ?: throw IllegalArgumentException("No user found for username $username")

    fun getUserRole(email: String): UserRole = userService.findOrCreateUserByEmail(email).userRole

    fun getUserDetails(userIds: List<Long>): List<UserDetailsDto> = userService.findUsersById(userIds).map { toUserDetailsDto(it) }
}
