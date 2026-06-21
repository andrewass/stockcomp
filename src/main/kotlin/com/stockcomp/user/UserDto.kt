package com.stockcomp.user

import com.stockcomp.user.internal.User

data class UserDetailsDto(
    val userId: Long,
    val username: String,
    val fullName: String? = null,
    val country: String? = null,
)

fun toUserDetailsDto(user: User) =
    UserDetailsDto(
        userId = requireNotNull(user.userId) { "User id is null while mapping UserDetailsDto" },
        fullName = user.fullName,
        username = user.username,
        country = user.country,
    )
