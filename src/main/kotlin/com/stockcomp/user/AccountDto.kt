package com.stockcomp.user

import com.stockcomp.user.internal.User
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

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

fun toAccountSettingsDto(user: User) =
    AccountSettingsDto(
        userId = requireNotNull(user.userId) { "User id is null while mapping AccountSettingsDto" },
        username = user.username,
        fullName = user.fullName,
        country = user.country,
        email = user.email,
    )
