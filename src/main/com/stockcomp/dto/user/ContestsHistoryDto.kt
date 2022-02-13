package com.stockcomp.dto.user

import com.stockcomp.domain.leaderboard.Medal

data class ContestsHistoryDto(
    val contestCount: Int = 0,
    val leaderboardRanking: Int? = null,
    val medals: List<Medal> = emptyList()
)
