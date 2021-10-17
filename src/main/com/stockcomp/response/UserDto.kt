package com.stockcomp.response

data class UserDto(
    val id: Long,
    val username: String,
    val email: String,
    val country: String,
    val userRole: String
)
