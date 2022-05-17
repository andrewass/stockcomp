package com.stockcomp.leaderboard.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.leaderboard.dto.LeaderboardEntryDto
import com.stockcomp.leaderboard.dto.toLeaderboardEntryDto
import com.stockcomp.leaderboard.entity.LeaderboardEntry
import com.stockcomp.leaderboard.repository.LeaderboardEntryRepository
import com.stockcomp.participant.repository.ParticipantRepository
import com.stockcomp.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultLeaderboardService(
    private val leaderboardEntryRepository: LeaderboardEntryRepository,
    private val participantRepository: ParticipantRepository,
    private val userService: UserService
) : LeaderboardService {

    private val logger = LoggerFactory.getLogger(DefaultLeaderboardService::class.java)

    override fun updateLeaderboard(contest: Contest) {
        logger.info("Starting update of leaderboard based on contest ${contest.contestNumber}")
        updateScoreForParticipants(contest)
        logger.info("Update of participant score completed")
        updateRankingForEntries()
        logger.info("Update of each ranking completed")
    }

    override fun getSortedLeaderboardEntries(): List<LeaderboardEntryDto> =
        leaderboardEntryRepository.findAllByOrderByRanking()
            .map { it.toLeaderboardEntryDto() }


    override fun getLeaderboardEntryForUser(username: String): LeaderboardEntryDto? =
        userService.findUserByUsername(username)
            .let { leaderboardEntryRepository.findByUser(it) }?.toLeaderboardEntryDto()


    private fun updateRankingForEntries() {
        var rank = 1
        leaderboardEntryRepository.findAllByOrderByScore()
            .onEach { it.ranking = rank++ }
            .also { leaderboardEntryRepository.saveAll(it) }
    }

    private fun updateScoreForParticipants(contest: Contest) {
        participantRepository.findAllByContest(contest)
            .forEach { participant ->
                val entry = leaderboardEntryRepository.findByUser(participant.user)
                    ?: LeaderboardEntry(user = participant.user)

                if (contest != entry.lastContest) {
                    entry.updateValues(participant, contest)
                    leaderboardEntryRepository.save(entry)
                }
            }
    }
}