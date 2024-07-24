package com.stockcomp.user

import com.stockcomp.user.domain.mapToUserDto
import com.stockcomp.user.internal.UserServiceInternal
import org.springframework.stereotype.Service

@Service
class UserServiceExternal(
    private val userService: UserServiceInternal
) {

    fun getUserIdByEmail(email: String): Long =
        userService.findUserByEmail(email)?.id
            ?: throw IllegalArgumentException("No user found for email $email")

    fun getUserIdByUsername(username: String): Long =
        userService.findUserByUsername(username).id
            ?: throw IllegalArgumentException("No user found for username $username")

    fun getUserByUserId(userId: Long): UserDto =
        mapToUserDto(userService.findUserById(userId))
}