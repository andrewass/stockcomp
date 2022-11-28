package com.stockcomp.user.service

import com.stockcomp.user.entity.User
import com.stockcomp.user.dto.UserDetailsDto

interface UserService {

    fun findUserByEmail(email: String): User

    fun findUserByUsername(username: String): User?

    fun updateUserDetails(userDetailsDto: UserDetailsDto)

    fun getUserDetails(username: String): UserDetailsDto

    fun verifyAdminUser(username: String) : Boolean
}