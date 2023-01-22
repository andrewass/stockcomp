package com.stockcomp.user.controller

import com.stockcomp.user.entity.User

fun mapToUserDetailsDto(src: User) =
    UserDetailsDto(
        email = src.email,
        fullName = src.fullName,
        username = src.username,
        country = src.country,
        userRole = src.userRole.name
    )
