package com.stockcomp.user.dto

import com.stockcomp.user.entity.User

fun mapToUserDetailsDto(src: User) =
    UserDetailsDto(
        id = src.id,
        email = src.email,
        displayName = src.fullName,
        country = src.country,
        userRole = src.userRole.name
    )
