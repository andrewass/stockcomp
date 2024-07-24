package com.stockcomp.user

data class UserPageDto(
    val users: List<UserDto>,
    val totalEntriesCount: Long
)
