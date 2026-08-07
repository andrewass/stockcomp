package com.stockcomp.user

interface UserServiceExternal {
    fun getUserIdBySubject(userSubject: String): Long

    fun getUserIdByUsername(username: String): Long

    fun getUserAuthenticationDetails(userSubject: String): UserAuthenticationDetails

    fun getUserDetails(userIds: List<Long>): List<UserDetailsDto>
}
