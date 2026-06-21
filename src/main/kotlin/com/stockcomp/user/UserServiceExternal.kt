package com.stockcomp.user

import com.stockcomp.user.internal.UserIdentityService
import com.stockcomp.user.internal.UserRole
import org.springframework.stereotype.Service

@Service
class UserServiceExternal(
    private val userIdentityService: UserIdentityService,
) {
    fun getUserIdBySubject(userSubject: String): Long =
        userIdentityService.findOrCreateUserBySubject(userSubject).userId
            ?: throw IllegalArgumentException("No user found for subject $userSubject")

    fun getUserIdByUsername(username: String): Long =
        userIdentityService.findUserByUsername(username).userId
            ?: throw IllegalArgumentException("No user found for username $username")

    fun getUserRole(userSubject: String): UserRole = userIdentityService.findOrCreateUserBySubject(userSubject).userRole

    fun getUserDetails(userIds: List<Long>): List<UserDetailsDto> = userIdentityService.findUsersById(userIds).map { toUserDetailsDto(it) }
}
