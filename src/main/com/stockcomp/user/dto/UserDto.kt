package com.stockcomp.user.dto

import com.stockcomp.user.entity.UserRole
import com.stockcomp.user.entity.UserStatus

data class UserDto(
    val username: String,
    val email: String,
    val userStatus: UserStatus,
    val userRole: UserRole
)
