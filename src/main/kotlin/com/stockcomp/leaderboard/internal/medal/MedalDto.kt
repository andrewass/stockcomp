package com.stockcomp.leaderboard.internal.medal

data class MedalDto(
    val medalValue: String,
    val position: Int
)

fun mapToMedalDto(src: Medal) =
    MedalDto(
        medalValue = src.medalValue.decode,
        position = src.position
    )