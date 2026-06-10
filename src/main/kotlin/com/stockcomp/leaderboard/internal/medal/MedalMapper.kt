package com.stockcomp.leaderboard.internal.medal

import com.stockcomp.leaderboard.MedalDto

fun mapToMedalDto(src: Medal) =
    MedalDto(
        medalValue = src.medalValue.decode,
        position = src.position,
    )
