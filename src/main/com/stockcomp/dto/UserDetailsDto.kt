package com.stockcomp.dto

import com.stockcomp.domain.leaderboard.Medal

data class UserDetailsDto(
    val id: Long? = null,
    val username: String,
    val fullName: String? = null,
    val country: String? = null,
    val userRole: String? = null,

    val contestCount: Int = 0,
    val leaderboardRanking: Int? = null,
    val medals: List<Medal> = emptyList()
)
