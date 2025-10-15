package com.stockcomp.leaderboard.leaderboard

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component

@Component
class LeaderboardInitializer(
    private val leaderboardRepository: LeaderboardRepository
) : ApplicationRunner {

    private val leaderboardId = 1L
    private val logger = LoggerFactory.getLogger(ApplicationRunner::class.java)

    override fun run(args: ApplicationArguments?) {
        if (!leaderboardRepository.existsById(leaderboardId)) {
            try {
                leaderboardRepository.save(Leaderboard(leaderboardId = leaderboardId))
            } catch (e: DataIntegrityViolationException) {
                logger.error(e.message)
            }
        }
    }
}