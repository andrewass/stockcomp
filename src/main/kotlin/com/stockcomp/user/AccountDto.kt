package com.stockcomp.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AccountSettingsDto(
    val userId: Long,
    val username: String,
    val fullName: String?,
    val country: String?,
    val email: String,
    val userStatus: UserStatus,
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

data class UpdateAccountStatusRequest(
    val newStatus: UserStatus,
)
