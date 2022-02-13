package com.stockcomp.dto.user

import com.stockcomp.dto.leaderboard.LeaderboardEntryDto

data class UserDetailsDto(
    val id: Long? = null,
    val username: String,
    val fullName: String? = null,
    val country: String? = null,
    val userRole: String,
    val leaderboardEntryDto: LeaderboardEntryDto? = null
)