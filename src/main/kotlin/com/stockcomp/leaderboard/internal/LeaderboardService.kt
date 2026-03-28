package com.stockcomp.leaderboard.internal

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
        val leaderboard = getLeaderboard()
        participantService.getParticipantsFromContest(contestId).forEach { participant ->
            val leaderboardEntry =
                leaderboardEntryRepository.findByUserId(participant.userId)
                    ?: leaderboardEntryRepository.save(
                        LeaderboardEntry(
                            leaderboard = leaderboard,
                            userId = participant.userId,
                        ),
                    )
            leaderboardEntry.incrementContestCount()
        }
        contestService.markContestAsCompleted(contestId)
    }

    fun getLeaderboard(): Leaderboard {
        val leaderboards = leaderboardRepository.findAll()
        if (leaderboards.size != 1) {
            throw IllegalStateException("Should only exist 1 leaderboard")
        }
        return leaderboards.first()
    }
}
