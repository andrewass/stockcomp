package com.stockcomp.user.internal

import com.stockcomp.user.AccountSettingsDto
import com.stockcomp.user.UserDetailsDto
import com.stockcomp.user.UserDto
import com.stockcomp.user.UserPageDto
import org.springframework.data.domain.Page

fun toUserDetailsDto(user: User) =
    UserDetailsDto(
        userId = requireNotNull(user.userId) { "User id is null while mapping UserDetailsDto" },
        fullName = user.fullName,
        username = user.username,
        country = user.country,
    )

fun toAccountSettingsDto(user: User) =
    AccountSettingsDto(
        userId = requireNotNull(user.userId) { "User id is null while mapping AccountSettingsDto" },
        username = user.username,
        fullName = user.fullName,
        country = user.country,
        email = user.email,
        userStatus = user.userStatus,
    )

fun mapToUserPageDto(source: Page<User>) =
    UserPageDto(
        entries = source.get().map { mapToUserDto(it) }.toList(),
        totalEntriesCount = source.totalElements,
    )

fun mapToUserDto(src: User) =
    UserDto(
        userId = requireNotNull(src.userId) { "User id is null while mapping UserDto" },
        username = src.username,
        email = src.email,
        userRole = src.userRole,
        userStatus = src.userStatus,
    )
