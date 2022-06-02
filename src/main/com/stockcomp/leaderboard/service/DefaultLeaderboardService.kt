package com.stockcomp.leaderboard.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.leaderboard.entity.LeaderboardEntry
import com.stockcomp.leaderboard.repository.LeaderboardEntryRepository
import com.stockcomp.participant.service.ParticipantService
import com.stockcomp.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultLeaderboardService(
    private val leaderboardEntryRepository: LeaderboardEntryRepository,
    private val participantService: ParticipantService,
    private val userService: UserService
) : LeaderboardService {

    private val logger = LoggerFactory.getLogger(DefaultLeaderboardService::class.java)

    override fun updateLeaderboard(contest: Contest) {
        logger.info("Starting update of leaderboard based on contest ${contest.contestNumber}")
        updateLeaderboardEntryValues(contest)
        logger.info("Update of participant score completed")
        updateRankingForLeaderboardEntries()
        logger.info("Update of each ranking completed")
    }

    override fun getSortedLeaderboardEntries(): List<LeaderboardEntry> =
        leaderboardEntryRepository.findAllByOrderByRanking()


    override fun getLeaderboardEntryForUser(username: String): LeaderboardEntry? =
        userService.findUserByUsername(username)
            .let { leaderboardEntryRepository.findByUser(it) }


    private fun updateLeaderboardEntryValues(contest: Contest) {
        participantService.getAllByContest(contest)
            .forEach { participant ->
                val entry = leaderboardEntryRepository.findByUser(participant.user)
                    ?: LeaderboardEntry(user = participant.user)

                if (contest != entry.lastContest) {
                    entry.updateValues(participant, contest)
                    leaderboardEntryRepository.save(entry)
                }
            }
    }

    private fun updateRankingForLeaderboardEntries() {
        var rank = 1
        leaderboardEntryRepository.findAllByOrderByScore()
            .onEach { it.ranking = rank++ }
            .also { leaderboardEntryRepository.saveAll(it) }
    }
}