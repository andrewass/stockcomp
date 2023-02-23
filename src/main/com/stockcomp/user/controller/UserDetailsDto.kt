package com.stockcomp.user.controller

import com.stockcomp.leaderboard.dto.LeaderboardEntryDto

data class UserDetailsDto(
    val email: String? = null,
    val username: String? = null,
    val fullName: String? = null,
    val country: String? = null,
    val userRole: String? = null,
    val leaderboardEntryDto: LeaderboardEntryDto? = null
)