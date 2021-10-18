package com.stockcomp.dto

data class UserDto(
    val id: Long,
    val username: String,
    val email: String,
    val userRole: String
)
