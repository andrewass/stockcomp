package com.stockcomp.user

import com.stockcomp.user.domain.UserRole
import com.stockcomp.user.domain.UserStatus

data class UserDto(
    val username: String,
    val email: String,
    val userStatus: UserStatus,
    val userRole: UserRole
)
