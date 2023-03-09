package com.stockcomp.user.controller

import com.stockcomp.user.entity.User

fun mapToUserDetailsDto(src: User) =
    UserDetailsDto(
        fullName = src.fullName,
        username = src.username,
        country = src.country,
    )
