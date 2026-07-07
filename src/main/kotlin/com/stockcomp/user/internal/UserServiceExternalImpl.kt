package com.stockcomp.user.internal

import com.stockcomp.user.UserDetailsDto
import com.stockcomp.user.UserRole
import com.stockcomp.user.UserServiceExternal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceExternalImpl(
    private val userIdentityService: UserIdentityService,
) : UserServiceExternal {
    @Transactional
    override fun getUserIdBySubject(userSubject: String): Long =
        userIdentityService.findOrCreateUserBySubject(userSubject).userId
            ?: throw IllegalArgumentException("No user found for subject $userSubject")

    override fun getUserIdByUsername(username: String): Long =
        userIdentityService.findUserByUsername(username).userId
            ?: throw IllegalArgumentException("No user found for username $username")

    @Transactional
    override fun getUserRole(userSubject: String): UserRole = userIdentityService.findOrCreateUserBySubject(userSubject).userRole

    override fun getUserDetails(userIds: List<Long>): List<UserDetailsDto> =
        userIdentityService.findUsersById(userIds).map {
            toUserDetailsDto(it)
        }
}
