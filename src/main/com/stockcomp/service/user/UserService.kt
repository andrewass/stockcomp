package com.stockcomp.service.user

import com.stockcomp.domain.user.User
import com.stockcomp.request.AuthenticationRequest
import com.stockcomp.request.SignUpRequest

interface UserService {

    fun signUpUser(request: SignUpRequest): User

    fun signInUser(request: AuthenticationRequest): String

    fun findUserByUsername(username: String): User
}