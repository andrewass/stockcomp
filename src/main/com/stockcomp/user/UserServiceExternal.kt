package com.stockcomp.user

import com.stockcomp.user.service.UserService
import org.springframework.stereotype.Service

@Service
class UserServiceExternal(
    private val userService: UserService
) {

    fun getUserIdByEmail(email: String): Long =
        userService.findUserByEmail(email)?.id
            ?: throw IllegalArgumentException("No user found for email $email")

    fun getUserIdByUsername(username: String): Long =
        userService.findUserByUsername(username).id
            ?: throw IllegalArgumentException("No user found for username $username")
}