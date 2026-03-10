package com.stockcomp.user

import com.stockcomp.user.internal.UserRole
import com.stockcomp.user.internal.UserServiceInternal
import org.springframework.stereotype.Service

@Service
class UserServiceExternal(
    private val userService: UserServiceInternal,
) {
    fun getUserIdBySubject(userSubject: String): Long =
        userService.findOrCreateUserBySubject(userSubject).userId
            ?: throw IllegalArgumentException("No user found for subject $userSubject")

    fun getUserIdByUsername(username: String): Long =
        userService.findUserByUsername(username).userId
            ?: throw IllegalArgumentException("No user found for username $username")

    fun getUserRole(userSubject: String): UserRole = userService.findOrCreateUserBySubject(userSubject).userRole

    fun getUserDetails(userIds: List<Long>): List<UserDetailsDto> = userService.findUsersById(userIds).map { toUserDetailsDto(it) }
}
