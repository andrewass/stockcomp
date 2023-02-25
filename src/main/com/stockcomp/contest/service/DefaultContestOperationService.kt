package com.stockcomp.contest.service

import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.entity.LeaderboardUpdateStatus
import com.stockcomp.investment.service.InvestmentProcessService
import com.stockcomp.investmentorder.service.InvestmentOrderProcessService
import com.stockcomp.leaderboard.service.LeaderboardOperationService
import com.stockcomp.participant.service.ParticipantService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DefaultContestOperationService(
    private val contestService: ContestService,
    private val investmentOrderProcessService: InvestmentOrderProcessService,
    private val investmentProcessService: InvestmentProcessService,
    private val leaderboardOperationService: LeaderboardOperationService,
    private val participantService: ParticipantService
) : ContestOperationService {

    override fun updateLeaderboard() {
        contestService.getContests(listOf(ContestStatus.COMPLETED))
            .filter { it.leaderboardUpdateStatus == LeaderboardUpdateStatus.AWAITING }
            .forEach {
                leaderboardOperationService.updateLeaderboardEntries(it)
                it.leaderboardUpdateStatus = LeaderboardUpdateStatus.COMPLETED
                contestService.saveContest(it)
            }
    }

    override fun maintainInvestments() {
        investmentProcessService.maintainInvestments()
        maintainParticipantValues()
    }

    override fun processInvestmentOrders() {
        investmentOrderProcessService.processInvestmentOrders()
    }

    override fun maintainContestStatus() {
        contestService.getContests(listOf(ContestStatus.AWAITING_START, ContestStatus.RUNNING, ContestStatus.STOPPED))
            .forEach {
                if (it.contestStatus == ContestStatus.AWAITING_START && it.startTime.isBefore(LocalDateTime.now())) {
                    it.contestStatus = ContestStatus.RUNNING
                    contestService.saveContest(it)
                }
                if (it.endTime.isBefore(LocalDateTime.now())) {
                    it.contestStatus = ContestStatus.COMPLETED
                    contestService.saveContest(it)
                }
            }
    }

    private fun maintainParticipantValues() {
        contestService.getContests(listOf(ContestStatus.AWAITING_START, ContestStatus.RUNNING, ContestStatus.STOPPED))
            .forEach { participantService.maintainParticipantValues(it) }
    }
}