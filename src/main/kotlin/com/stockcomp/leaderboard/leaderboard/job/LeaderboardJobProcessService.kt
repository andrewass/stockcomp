package com.stockcomp.leaderboard.leaderboard.job

import com.stockcomp.leaderboard.leaderboard.LeaderboardEntryRepository
import com.stockcomp.leaderboard.leaderboard.LeaderboardService
import com.stockcomp.participant.ParticipantServiceExternal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LeaderboardJobProcessService(
    private val leaderboardEntryRepository: LeaderboardEntryRepository,
    private val leaderboardJobStateService: LeaderboardJobStateService,
    private val leaderboardService: LeaderboardService,
    private val participantService: ParticipantServiceExternal,
) {

    @Transactional
    fun processJob(job: LeaderboardJob) {
        try {
            leaderboardService.getLeaderboard()
            participantService.getParticipantsFromContest(job.contestId).forEach { participant ->
                val leaderboardEntry = leaderboardEntryRepository.findByUserId(participant.userId)
                leaderboardEntry.incrementContestCount()
            }
            leaderboardJobStateService.markAsCompleted(job)
        } catch (e: Exception) {
            leaderboardJobStateService.markAsFailed(job)
            throw e
        }
    }
}