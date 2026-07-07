package com.stockcomp.user

interface UserServiceExternal {
    fun getUserIdBySubject(userSubject: String): Long

    fun getUserIdByUsername(username: String): Long

    fun getUserRole(userSubject: String): UserRole

    fun getUserDetails(userIds: List<Long>): List<UserDetailsDto>
}
