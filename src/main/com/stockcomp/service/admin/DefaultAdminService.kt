package com.stockcomp.service.admin

import com.fasterxml.jackson.databind.ObjectMapper
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.UserRepository
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
        val contests = contestRepository.findAllByCompletedIsFalseOrRunningIsTrue()

        return contests.map { objectMapper.convertValue(it, ContestDto::class.java) }
    }

    override fun getContest(id: Long): ContestDto {
        val contest = contestRepository.findById(id)

        return objectMapper.convertValue(contest.get(), ContestDto::class.java)
    }

    override fun updateContest(contestDto: ContestDto) {
        val contest = contestRepository.findById(contestDto.id).get()
        contest.apply {
            isRunning = contestDto.isRunning
            isCompleted = contestDto.isCompleted
        }
        contestRepository.save(contest)
    }


    override fun getUsers(): List<UserDto> {
        val users = userRepository.findAll()

        return users.map { objectMapper.convertValue(it, UserDto::class.java) }
    }
}