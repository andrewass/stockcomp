package com.stockcomp.user

import com.stockcomp.user.internal.User
import com.stockcomp.user.internal.UserRole
import com.stockcomp.user.internal.UserStatus
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.data.domain.Page

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

fun mapToUserPageDto(source: Page<User>) =
    UserPageDto(
        entries = source.get().map { mapToUserDto(it) }.toList(),
        totalEntriesCount = source.totalElements,
    )

fun mapToUserDto(src: User) =
    UserDto(
        userId = requireNotNull(src.userId) { "User id is null while mapping UserDto" },
        username = src.username,
        email = src.email,
        userRole = src.userRole,
        userStatus = src.userStatus,
    )
