package com.stockcomp.service.admin

import com.fasterxml.jackson.databind.ObjectMapper
import com.stockcomp.domain.contest.Contest
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.UserRepository
import com.stockcomp.request.CreateContestRequest
import com.stockcomp.response.ContestDto
import com.stockcomp.response.UserDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultAdminService(
    private val contestRepository: ContestRepository,
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper = ObjectMapper()
) : AdminService {

    override fun getRunningAndUpcomingContests(): List<ContestDto> {
        val contests = contestRepository.findAllByCompletedIsFalse()

        return contests.map { objectMapper.convertValue(it, ContestDto::class.java) }
    }

    override fun getContest(id: Long): ContestDto {
        val contest = contestRepository.findById(id)

        return objectMapper.convertValue(contest.get(), ContestDto::class.java)
    }

    override fun createContest(request: CreateContestRequest) {
        val contest = Contest(
            contestNumber = request.contestNumber,
            startTime = request.startTime
        )
        contestRepository.save(contest)
    }


    override fun updateContest(contestDto: ContestDto) {
        val contest = contestRepository.findById(contestDto.id).get()
        contest.apply {
            running = contestDto.running
            completed = contestDto.completed
        }
        contestRepository.save(contest)
    }


    override fun getUsers(): List<UserDto> {
        val users = userRepository.findAll()

        return users.map { objectMapper.convertValue(it, UserDto::class.java) }
    }
}