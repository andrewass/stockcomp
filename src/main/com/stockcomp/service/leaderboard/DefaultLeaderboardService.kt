package com.stockcomp.service.leaderboard

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.leaderboard.LeaderboardEntry
import com.stockcomp.domain.leaderboard.Medal
import com.stockcomp.domain.leaderboard.MedalValue
import com.stockcomp.repository.LeaderboardEntryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.ceil

@Service
@Transactional
class DefaultLeaderboardService(
    private val leaderboardEntryRepository: LeaderboardEntryRepository
) : LeaderboardService {

    private val logger = LoggerFactory.getLogger(DefaultLeaderboardService::class.java)

    override fun updateLeaderboard(contest: Contest) {
        logger.info("Starting update of leaderboard based on contest ${contest.contestNumber}")
        updateScoreForParticipants(contest)
        logger.info("Update of participant score completed")
        CoroutineScope(Default).launch {
            updateRankingForEntries()
        }
    }

    override fun getSortedLeaderboard(): List<LeaderboardEntry> {
        return leaderboardEntryRepository.findAllByOrderByRanking()
    }

    private fun updateRankingForEntries() {
        var rank = 1
        val entries = leaderboardEntryRepository.findAllByOrderByScore()
        entries.forEach { it.ranking = rank++ }
        leaderboardEntryRepository.saveAll(entries)
        logger.info("Update of each ranking completed")
    }

    private fun updateScoreForParticipants(contest: Contest) {
        val medalMap = createMedalMap(contest)
        contest.participants.forEach { participant ->
            val leaderboardEntry = leaderboardEntryRepository.findByUser(participant.user)
                ?: LeaderboardEntry(user = participant.user)
            if (contest != leaderboardEntry.lastContest) {
                val participantScore = (participant.rank!! / contest.participantCount).toDouble()

                leaderboardEntry.apply {
                    this.score += participantScore
                    this.contestCount = this.contestCount + 1
                    this.lastContest = contest
                }
                updateMedalsForEntry(leaderboardEntry, participant, contest, medalMap)
                leaderboardEntryRepository.save(leaderboardEntry)
            }
        }
    }

    private fun createMedalMap(contest: Contest): HashMap<Double, MedalValue> {
        val basePercentage = (100 / contest.participantCount).toDouble()
        return hashMapOf(
            ceil(5 / basePercentage) to MedalValue.GOLD,
            ceil(10 / basePercentage) to MedalValue.SILVER,
            ceil(15 / basePercentage) to MedalValue.BRONZE
        )
    }

    private fun updateMedalsForEntry(
        leaderboardEntry: LeaderboardEntry, participant: Participant,
        contest: Contest, medalMap: Map<Double, MedalValue>
    ) {
        medalMap.entries.firstOrNull { (key, _) -> key <= participant.rank!! }?.let {
            leaderboardEntry.addMedal(
                Medal(
                    contest = contest,
                    leaderboardEntry = leaderboardEntry,
                    medalValue = it.value,
                    position = participant.rank!!
                )
            )
        }
    }
}
