package com.stockcomp.user.domain

import com.stockcomp.user.UserDetailsDto
import com.stockcomp.user.UserDto
import com.stockcomp.user.UserPageDto
import org.springframework.data.domain.Page

fun mapToUserPageDto(source: Page<User>) = UserPageDto(
    users = source.get().map { mapToUserDto(it) }.toList(),
    totalEntriesCount = source.totalElements
)

fun mapToUserDto(src: User) =
    UserDto(
        username = src.username,
        email = src.email,
        userRole = src.userRole,
        userStatus = src.userStatus
    )

fun mapToUserDetailsDto(src: User) =
    UserDetailsDto(
        fullName = src.fullName,
        username = src.username,
        country = src.country,
    )
