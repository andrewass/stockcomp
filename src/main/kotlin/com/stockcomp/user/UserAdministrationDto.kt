package com.stockcomp.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserDto(
    val userId: Long,
    val username: String,
    val email: String,
    val userStatus: UserStatus,
    val userRole: UserRole,
)

data class UserPageDto(
    val entries: List<UserDto>,
    val totalEntriesCount: Long,
)

data class CreateUserRequest(
    @field:NotBlank
    @field:Email
    val email: String,
)
