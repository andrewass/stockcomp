package com.stockcomp.leaderboard.internal

import com.stockcomp.common.competitionRanksForSortedValues
import com.stockcomp.contest.ContestServiceExternal
import com.stockcomp.leaderboard.internal.entry.LeaderboardEntry
import com.stockcomp.leaderboard.internal.entry.LeaderboardEntryRepository
import com.stockcomp.participant.ParticipantServiceExternal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LeaderboardService(
    private val leaderboardRepository: LeaderboardRepository,
    private val leaderboardEntryRepository: LeaderboardEntryRepository,
    private val participantService: ParticipantServiceExternal,
    private val contestService: ContestServiceExternal,
) {
    fun updateLeaderboard(contestId: Long) {
        if (!contestService.lockContestForCompletion(contestId)) {
            return
        }

        val leaderboard = getLeaderboard()
        participantService.rankParticipantsForContest(contestId).forEach { participant ->
            val leaderboardEntry =
                leaderboardEntryRepository.findByUserId(participant.userId)
                    ?: leaderboardEntryRepository.save(
                        LeaderboardEntry(
                            leaderboard = leaderboard,
                            userId = participant.userId,
                        ),
                    )
            leaderboardEntry.recordContestResult(
                contestId = contestId,
                position = requireNotNull(participant.rank) { "Rank must be assigned before leaderboard update" },
            )
        }

        recalculateRankings()
        leaderboard.incrementContestCount()
        contestService.markContestAsCompleted(contestId)
    }

    fun getLeaderboard(): Leaderboard {
        val leaderboards = leaderboardRepository.findAll()
        if (leaderboards.size != 1) {
            throw IllegalStateException("Should only exist 1 leaderboard")
        }
        return leaderboards.first()
    }

    private fun recalculateRankings() {
        val entries = leaderboardEntryRepository.findAllByOrderByScoreDescAndUserIdAsc()
        val rankings = competitionRanksForSortedValues(entries.map { it.score() })
        entries.zip(rankings).forEach { (entry, ranking) -> entry.assignRanking(ranking) }
    }
}
