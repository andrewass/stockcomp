package com.stockcomp.service.admin

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.dto.ContestDto
import com.stockcomp.dto.UserDto
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.UserRepository
import com.stockcomp.request.CreateContestRequest
import com.stockcomp.service.investment.MaintainParticipantsService
import com.stockcomp.service.leaderboard.LeaderboardService
import com.stockcomp.service.order.OrderProcessingService
import com.stockcomp.util.toContestDto
import com.stockcomp.util.toUserDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultAdminService(
    private val leaderboardService: LeaderboardService,
    private val maintainParticipantsService: MaintainParticipantsService,
    private val orderProcessingService: OrderProcessingService,
    private val contestRepository: ContestRepository,
    private val userRepository: UserRepository
) : AdminService {

    private val pass = Unit

    override fun getAllContests(): List<ContestDto> =
        contestRepository.findAll()
            .map { it.toContestDto() }

    override fun getContest(id: Long): ContestDto =
        contestRepository.findById(id).get().toContestDto()

    override fun createContest(request: CreateContestRequest): ContestDto =
        Contest(
            contestNumber = request.contestNumber,
            startTime = request.startTime,
            endTime = request.startTime.plusMonths(2L)
        ).let { contest -> contestRepository.save(contest) }.toContestDto()

    override fun deleteContest(id: Long): ContestDto =
        contestRepository.findById(id).get().let {
            contestRepository.delete(it)
            it.toContestDto()
        }

    override fun getCompletedContests(): List<ContestDto> =
        contestRepository.findAllByContestStatus(ContestStatus.COMPLETED)
            .map { it.toContestDto() }

    override fun updateLeaderboard(contestNumber: Int) {
        contestRepository.findByContestNumber(contestNumber)
            .also { leaderboardService.updateLeaderboard(it) }
    }

    override fun updateContestStatus(contestDto: ContestDto): ContestDto =
        contestRepository.findById(contestDto.id).get()
            .let {
                when (ContestStatus.fromDecode(contestDto.contestStatus)) {
                    ContestStatus.COMPLETED -> {
                        orderProcessingService.terminateRemainingOrders(it)
                        leaderboardService.updateLeaderboard(it)
                    }
                    ContestStatus.STOPPED -> {
                        orderProcessingService.stopOrderProcessing()
                        maintainParticipantsService.stopParticipantsMaintenance()
                    }
                    ContestStatus.RUNNING -> {
                        orderProcessingService.startOrderProcessing()
                        maintainParticipantsService.startParticipantsMaintenance()
                    }
                    ContestStatus.AWAITING_START -> pass
                    else -> pass
                }
                it.contestStatus = ContestStatus.fromDecode(contestDto.contestStatus)!!
                contestRepository.save(it).toContestDto()
            }

    override fun getUsers(): List<UserDto> =
        userRepository.findAll()
            .map { it.toUserDto() }
}