package com.stockcomp.user.dto

data class UserPageDto(
    val users: List<UserDto>,
    val totalEntriesCount: Long
)
