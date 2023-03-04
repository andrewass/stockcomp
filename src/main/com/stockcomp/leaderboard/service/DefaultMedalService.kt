package com.stockcomp.leaderboard.service

import com.stockcomp.leaderboard.entity.Medal
import com.stockcomp.leaderboard.repository.MedalRepository
import org.springframework.stereotype.Service

@Service
class DefaultMedalService(
    private val medalRepository: MedalRepository
) : MedalService {

    override fun saveMedal(medal: Medal) {
        medalRepository.save(medal)
    }

}