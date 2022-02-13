package com.stockcomp.dto.user

import com.stockcomp.domain.leaderboard.Medal

data class UserDetailsDto(
    val id: Long? = null,
    val username: String,
    val fullName: String? = null,
    val country: String? = null,
    val userRole: String,

    val contestCount: Int = 0,
    val leaderboardRanking: Int? = null,
    val medals: List<Medal> = emptyList()
)