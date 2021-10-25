package com.stockcomp.service.admin

import com.stockcomp.domain.contest.Contest
import com.stockcomp.dto.ContestDto
import com.stockcomp.dto.UserDto
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.UserRepository
import com.stockcomp.request.CreateContestRequest
import com.stockcomp.service.leaderboard.LeaderboardService
import com.stockcomp.service.order.DefaultOrderProcessingService
import com.stockcomp.service.order.OrderProcessingService
import com.stockcomp.util.toContestDto
import com.stockcomp.util.toUserDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultAdminService(
    private val leaderboardService: LeaderboardService,
    private val orderProcessingService: OrderProcessingService,
    private val contestRepository: ContestRepository,
    private val userRepository: UserRepository
) : AdminService {

    override fun getAllContests(): List<ContestDto> {
        val contests = contestRepository.findAll()

        return contests.map { it.toContestDto() }
    }

    override fun getContest(id: Long): ContestDto {
        val contest = contestRepository.findById(id).get()

        return contest.toContestDto()
    }

    override fun createContest(request: CreateContestRequest): ContestDto {
        val contest = Contest(
            contestNumber = request.contestNumber,
            startTime = request.startTime,
            endTime = request.startTime.plusMonths(2L)
        )
        val persistedContest = contestRepository.save(contest)

        return persistedContest.toContestDto()
    }

    override fun deleteContest(id: Long): ContestDto {
        val contest = contestRepository.findById(id).get()
        contestRepository.delete(contest)

        return contest.toContestDto()
    }

    override fun getCompletedContests(): List<ContestDto> {
        val contests = contestRepository.findAllByCompleted(true)

        return contests.map { it.toContestDto() }
    }

    override fun updateLeaderboard(contestId: Long) {
        val contest = contestRepository.findById(contestId).get()
        leaderboardService.updateLeaderboard(contest)
    }

    override fun updateContest(contestDto: ContestDto): ContestDto {
        val contest = contestRepository.findById(contestDto.id).get()
        //TODO: Update contest with new status
        return contestRepository.save(contest).toContestDto()
    }

    override fun getUsers(): List<UserDto> {
        val users = userRepository.findAll()

        return users.map { it.toUserDto() }
    }


}