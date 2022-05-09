package com.stockcomp.user.service

import com.stockcomp.user.entity.User
import com.stockcomp.user.dto.UserDetailsDto
import com.stockcomp.request.AuthenticationRequest
import com.stockcomp.request.SignUpRequest

interface UserService {

    fun signUpUser(request: SignUpRequest): User

    fun signInUser(request: AuthenticationRequest): String

    fun findUserByUsername(username: String): User?

    fun updateUserDetails(userDetailsDto: UserDetailsDto)

    fun getUserDetails(username: String): UserDetailsDto

    fun verifyAdminUser(username: String) : Boolean
}