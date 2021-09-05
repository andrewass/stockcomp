package com.stockcomp.service.admin

import com.stockcomp.domain.contest.Contest
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.UserRepository
import com.stockcomp.request.CreateContestRequest
import com.stockcomp.response.ContestDto
import com.stockcomp.response.UserDto
import com.stockcomp.service.util.toContestDto
import com.stockcomp.service.util.toUserDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultAdminService(
    private val contestRepository: ContestRepository,
    private val userRepository: UserRepository
) : AdminService {

    override fun getRunningAndUpcomingContests(): List<ContestDto> {
        val contests = contestRepository.findAllByCompletedIsFalse()

        return contests.map { it.toContestDto() }
    }

    override fun getContest(id: Long): ContestDto {
        val contest = contestRepository.findById(id).get()

        return contest.toContestDto()
    }

    override fun createContest(request: CreateContestRequest): ContestDto {
        val contest = Contest(
            contestNumber = request.contestNumber,
            startTime = request.startTime
        )
        val persistedContest = contestRepository.save(contest)

        return persistedContest.toContestDto()
    }

    override fun deleteContest(id: Long): ContestDto {
        val contest = contestRepository.findById(id).get()
        contestRepository.delete(contest)

        return contest.toContestDto()
    }

    override fun updateContest(contestDto: ContestDto): ContestDto {
        val contest = contestRepository.findById(contestDto.id).get()
        contest.apply {
            running = contestDto.running
            completed = contestDto.completed
        }
        val persistedContest = contestRepository.save(contest)

        return persistedContest.toContestDto()
    }

    override fun getUsers(): List<UserDto> {
        val users = userRepository.findAll()

        return users.map { it.toUserDto() }
    }
}