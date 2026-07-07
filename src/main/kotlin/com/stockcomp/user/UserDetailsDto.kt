package com.stockcomp.user

data class UserDetailsDto(
    val userId: Long,
    val username: String,
    val fullName: String? = null,
    val country: String? = null,
)
