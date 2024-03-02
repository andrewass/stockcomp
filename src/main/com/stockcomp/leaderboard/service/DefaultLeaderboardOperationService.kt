package com.stockcomp.leaderboard.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.leaderboard.entity.LeaderboardEntry
import com.stockcomp.leaderboard.entity.Medal
import com.stockcomp.leaderboard.entity.MedalValue
import com.stockcomp.participant.entity.Participant
import com.stockcomp.participant.ParticipantService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultLeaderboardOperationService(
    private val participantService: ParticipantService,
    private val leaderboardService: LeaderboardService,
    private val medalService: MedalService
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
                    entry.contestCount += 1
                    entry.score += (contest.participantCount - participant.rank + 1)
                    entry.score /= entry.contestCount
                    entry.lastContest = contest
                    leaderboardService.saveEntry(entry)
                    updateMedals(participant, contest, entry)
                }
            }
    }

    private fun updateRankingForLeaderboardEntries() {
        var rank = 1
        leaderboardService.getLeaderboardentriesByOrderByScore()
            .onEach { it.ranking = rank++ }
            .also { leaderboardService.saveAllEntries(it) }
    }

    private fun updateMedals(participant: Participant, contest: Contest, entry: LeaderboardEntry) {
        getMedalBasedOnPositionPercentage(participant.rank)
            ?.also {
                medalService.saveMedal(
                    Medal(
                        contest = contest,
                        leaderboardEntry = entry,
                        medalValue = it,
                        position = participant.rank
                    )
                )
            }
    }

    private fun getMedalBasedOnPositionPercentage(rank: Int): MedalValue? {
        return when (rank) {
            1 -> MedalValue.GOLD
            2 -> MedalValue.SILVER
            3 -> MedalValue.BRONZE
            else -> null
        }
    }
}