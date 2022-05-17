package com.stockcomp.user.dto

import com.stockcomp.user.entity.User

fun mapToUserDetailsDto(src: User) =
    UserDetailsDto(
        id = src.id,
        username = src.username,
        country = src.country,
        fullName = src.fullName,
        userRole = src.userRole.name
    )
