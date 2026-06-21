package com.stockcomp.user

import com.stockcomp.user.internal.User
import com.stockcomp.user.internal.UserRole
import com.stockcomp.user.internal.UserStatus
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.data.domain.Page

data class UserDto(
    val userId: Long,
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

data class AccountSettingsDto(
    val userId: Long,
    val username: String,
    val fullName: String?,
    val country: String?,
    val email: String,
)

data class UpdateAccountSettingsRequest(
    @field:NotBlank
    @field:Size(max = 50)
    val username: String,
    @field:Size(max = 100)
    val fullName: String? = null,
    @field:Size(max = 100)
    val country: String? = null,
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

fun toUserDetailsDto(user: User) =
    UserDetailsDto(
        userId = requireNotNull(user.userId) { "User id is null while mapping UserDetailsDto" },
        fullName = user.fullName,
        username = user.username,
        country = user.country,
    )

fun toAccountSettingsDto(user: User) =
    AccountSettingsDto(
        userId = requireNotNull(user.userId) { "User id is null while mapping AccountSettingsDto" },
        username = user.username,
        fullName = user.fullName,
        country = user.country,
        email = user.email,
    )
