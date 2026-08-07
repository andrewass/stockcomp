package com.stockcomp.user

data class UserAuthenticationDetails(
    val role: UserRole,
    val status: UserStatus,
)
