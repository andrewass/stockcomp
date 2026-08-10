package com.stockcomp.user

interface UserServiceExternal {
    fun getUserIdByExternalIdentity(externalIdentity: ExternalIdentity): Long

    fun getUserIdByUsername(username: String): Long

    fun getUserAuthenticationDetails(externalIdentity: ExternalIdentity): UserAuthenticationDetails

    fun getUserDetails(userIds: List<Long>): List<UserDetailsDto>
}
