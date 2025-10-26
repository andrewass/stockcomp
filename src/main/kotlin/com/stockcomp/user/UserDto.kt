package com.stockcomp.user

import com.stockcomp.user.internal.User
import com.stockcomp.user.internal.UserRole
import com.stockcomp.user.internal.UserStatus
import org.springframework.data.domain.Page

data class UserDto(
    val username: String,
    val email: String,
    val userStatus: UserStatus,
    val userRole: UserRole,
)

data class UserDetailsDto(
    val userId: Long,
    val username: String,
    val fullName: String? = null,
    val country: String? = null,
)

data class UserPageDto(
    val users: List<UserDto>,
    val totalEntriesCount: Long,
)

fun mapToUserPageDto(source: Page<User>) =
    UserPageDto(
        users = source.get().map { mapToUserDto(it) }.toList(),
        totalEntriesCount = source.totalElements,
    )

fun mapToUserDto(src: User) =
    UserDto(
        username = src.username,
        email = src.email,
        userRole = src.userRole,
        userStatus = src.userStatus,
    )

fun toUserDetailsDto(user: User) =
    UserDetailsDto(
        userId = user.id!!,
        fullName = user.fullName,
        username = user.username,
        country = user.country,
    )
