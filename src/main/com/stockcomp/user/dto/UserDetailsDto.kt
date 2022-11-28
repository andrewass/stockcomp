package com.stockcomp.user.dto

import com.stockcomp.leaderboard.dto.LeaderboardEntryDto

data class UserDetailsDto(
    val id: Long? = null,
    val email: String,
    val displayName: String? = null,
    val country: String? = null,
    val userRole: String? = null,
    val leaderboardEntryDto: LeaderboardEntryDto? = null
)