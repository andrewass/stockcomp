package com.stockcomp.leaderboard.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.participant.entity.Participant
import com.stockcomp.domain.contest.enums.LeaderboardUpdateStatus
import com.stockcomp.leaderboard.entity.LeaderboardEntry
import com.stockcomp.leaderboard.entity.Medal
import com.stockcomp.leaderboard.entity.MedalValue
import com.stockcomp.leaderboard.dto.LeaderboardEntryDto
import com.stockcomp.contest.repository.ContestRepository
import com.stockcomp.leaderboard.repository.LeaderboardEntryRepository
import com.stockcomp.participant.repository.ParticipantRepository
import com.stockcomp.user.service.UserService
import com.stockcomp.util.toLeaderboardEntryDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultLeaderboardService(
    private val leaderboardEntryRepository: LeaderboardEntryRepository,
    private val contestRepository: ContestRepository,
    private val participantRepository: ParticipantRepository,
    private val userService: UserService
) : LeaderboardService {

    private val logger = LoggerFactory.getLogger(DefaultLeaderboardService::class.java)

    private val medalMap = hashMapOf(
        0.05 to MedalValue.GOLD,
        0.10 to MedalValue.SILVER,
        0.15 to MedalValue.BRONZE
    )

    override fun updateLeaderboard(contest: Contest) {
        if (contest.leaderboardUpdateStatus != LeaderboardUpdateStatus.COMPLETED) {
            logger.info("Starting update of leaderboard based on contest ${contest.contestNumber}")
            contest.leaderboardUpdateStatus = LeaderboardUpdateStatus.IN_PROGRESS

            CoroutineScope(Dispatchers.Default).launch {
                updateScoreForParticipants(contest)
                logger.info("Update of participant score completed")
                updateRankingForEntries()
                logger.info("Update of each ranking completed")
            }
        }
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
                    updateAndSaveLeaderboardEntry(participant, contest, entry)
                }
            }
        contest.leaderboardUpdateStatus = LeaderboardUpdateStatus.COMPLETED
        contestRepository.save(contest)
    }

    private fun updateAndSaveLeaderboardEntry(participant: Participant, contest: Contest, entry: LeaderboardEntry) {
        val participantScore = participant.rank / contest.participantCount
        entry.apply {
            score += participantScore
            contestCount += 1
            lastContest = contest
        }
        updateMedalsForEntry(entry, participant, contest)
        leaderboardEntryRepository.save(entry)
    }

    private fun updateMedalsForEntry(entry: LeaderboardEntry, participant: Participant, contest: Contest) {
        medalMap.entries
            .firstOrNull { (key, _) -> key <= getParticipantPercentagePosition(participant, contest) }
            ?.let {
                entry.addMedal(
                    Medal(
                        contest = contest,
                        leaderboardEntry = entry,
                        medalValue = it.value,
                        position = participant.rank
                    )
                )
            }
    }

    private fun getParticipantPercentagePosition(participant: Participant, contest: Contest): Double =
        ((participant.rank - 1) / contest.participantCount).toDouble()

}