package com.stockcomp.leaderboard.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.leaderboard.entity.LeaderboardEntry
import com.stockcomp.participant.service.ParticipantService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DefaultLeaderboardOperationService(
    private val participantService: ParticipantService,
    private val leaderboardService: LeaderboardService
) : LeaderboardOperationService {

    private val logger = LoggerFactory.getLogger(DefaultLeaderboardService::class.java)

    override fun updateLeaderboardEntries(contest: Contest) {
        logger.info("Starting update of leaderboard based on contest ${contest.contestNumber}")
        updateLeaderboardEntryValues(contest)
        logger.info("Update of participant score completed")
        updateRankingForLeaderboardEntries()
        logger.info("Update of each ranking completed")
    }

    private fun updateLeaderboardEntryValues(contest: Contest) {
        participantService.getAllByContest(contest)
            .forEach { participant ->
                val entry = leaderboardService.getLeaderboardEntryForUser(participant.user)
                    ?: LeaderboardEntry(user = participant.user)

                if (contest != entry.lastContest) {
                    entry.updateValues(participant, contest)
                    leaderboardService.saveEntry(entry)
                }
            }
    }

    private fun updateRankingForLeaderboardEntries() {
        var rank = 1
        leaderboardService.getLeaderboardentriesByOrderByScore()
            .onEach { it.ranking = rank++ }
            .also { leaderboardService.saveAllEntries(it) }
    }
}