package com.stockcomp.user.service

import com.stockcomp.user.controller.UserDetailsDto
import com.stockcomp.user.entity.User

interface UserService {

    fun findUserByEmail(email: String): User

    fun findUserByTokenClaim(email: String): User

    fun findUserByUsername(username: String): User

    fun updateUser(user: User, userDetailsDto: UserDetailsDto)

    fun getUserDetails(username: String): UserDetailsDto

    fun verifyAdminUser(username: String): Boolean
}