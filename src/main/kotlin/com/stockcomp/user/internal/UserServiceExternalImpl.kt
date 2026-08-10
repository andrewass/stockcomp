package com.stockcomp.user.internal

import com.stockcomp.user.ExternalIdentity
import com.stockcomp.user.UserAuthenticationDetails
import com.stockcomp.user.UserDetailsDto
import com.stockcomp.user.UserServiceExternal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceExternalImpl(
    private val userIdentityService: UserIdentityService,
) : UserServiceExternal {
    @Transactional
    override fun getUserIdByExternalIdentity(externalIdentity: ExternalIdentity): Long =
        userIdentityService.findOrCreateUserByExternalIdentity(externalIdentity).userId
            ?: throw IllegalArgumentException("No user found for external identity $externalIdentity")

    override fun getUserIdByUsername(username: String): Long =
        userIdentityService.findUserByUsername(username).userId
            ?: throw IllegalArgumentException("No user found for username $username")

    @Transactional
    override fun getUserAuthenticationDetails(externalIdentity: ExternalIdentity): UserAuthenticationDetails =
        userIdentityService
            .findOrCreateUserByExternalIdentity(externalIdentity)
            .let { user ->
                UserAuthenticationDetails(
                    role = user.userRole,
                    status = user.userStatus,
                )
            }

    override fun getUserDetails(userIds: List<Long>): List<UserDetailsDto> =
        userIdentityService.findUsersById(userIds).map {
            toUserDetailsDto(it)
        }
}
