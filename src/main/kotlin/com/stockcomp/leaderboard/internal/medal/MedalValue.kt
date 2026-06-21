package com.stockcomp.leaderboard.internal.medal

enum class MedalValue(
    val decode: String,
    val points: Int,
) {
    GOLD("Gold", 3),
    SILVER("Silver", 2),
    BRONZE("Bronze", 1),
}
