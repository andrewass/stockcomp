package com.stockcomp.service.admin

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.LeaderboardUpdateStatus
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.UserRepository
import com.stockcomp.request.CreateContestRequest
import com.stockcomp.response.ContestDto
import com.stockcomp.response.UserDto
import com.stockcomp.service.leaderboard.LeaderboardService
import com.stockcomp.util.toContestDto
import com.stockcomp.util.toUserDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultAdminService(
    private val contestRepository: ContestRepository,
    private val userRepository: UserRepository,
    private val leaderboardService: LeaderboardService
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

    override fun updateContest(contestDto: ContestDto): ContestDto {
        val contest = contestRepository.findById(contestDto.id).get()
        contest.apply {
            running = contestDto.running
            completed = contestDto.completed
        }
        if(contest.leaderboardUpdateStatus == LeaderboardUpdateStatus.AWAITING){
            leaderboardService.updateLeaderboard(contest)
        }
        val persistedContest = contestRepository.save(contest)

        return persistedContest.toContestDto()
    }

    override fun getUsers(): List<UserDto> {
        val users = userRepository.findAll()

        return users.map { it.toUserDto() }
    }
}