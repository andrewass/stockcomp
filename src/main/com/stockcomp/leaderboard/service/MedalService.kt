package com.stockcomp.leaderboard.service

import com.stockcomp.leaderboard.entity.Medal

interface MedalService {

    fun saveMedal(medal: Medal)
}