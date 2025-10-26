package com.stockcomp.leaderboard

import com.stockcomp.leaderboard.internal.LeaderboardService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LeaderboardServiceExternal {
    private val logger = LoggerFactory.getLogger(LeaderboardService::class.java)

    fun updateLeaderboardEntries(contestId: Long) {
        // logger.info("Starting update of leaderboard based on contest ${contest.contestId}")
        // updateLeaderboardEntryValues(contest)
        logger.info("Update of participant score completed")
        // updateRankingForLeaderboardEntries()
        logger.info("Update of each ranking completed")
    }

    /*
    private fun updateLeaderboardEntryValues(contest: Contest) {
        participantService.getAllByContest(contest.contestId!!)
            .map { updatedLeaderboardEntry(it, contest) }
            .also { leaderboardService.saveAllEntries(it) }
    }

    private fun updatedLeaderboardEntry(participant: Participant, contest: Contest): LeaderboardEntry {
        val entry = leaderboardService.getLeaderboardEntryForUser(participant.user)
            ?: LeaderboardEntry(user = participant.user)
        entry.contestCount += 1
        entry.score += (contest.getParticipantCount() - participant.rank!! + 1)
        entry.score /= entry.contestCount
        entry.lastContest = contest
        updateMedals(participant, contest, entry)
        return entry
    }

    private fun updateMedals(participant: Participant, contest: Contest, entry: LeaderboardEntry) {
        getMedalBasedOnPositionPercentage(participant.rank!!)
            ?.also { value ->
                Medal(
                    contest = contest,
                    leaderboardEntry = entry,
                    medalValue = value,
                    position = participant.rank!!
                ).also { entry.medals.add(it) }
            }
    }

    private fun updateRankingForLeaderboardEntries() {
        var rank = 1
        leaderboardService.getLeaderboardentriesByOrderByScore()
            .onEach { it.ranking = rank++ }
            .also { leaderboardService.saveAllEntries(it) }
    }

    private fun getMedalBasedOnPositionPercentage(rank: Int): MedalValue? {
        return when (rank) {
            1 -> MedalValue.GOLD
            2 -> MedalValue.SILVER
            3 -> MedalValue.BRONZE
            else -> null
        }
    }
     */
}
