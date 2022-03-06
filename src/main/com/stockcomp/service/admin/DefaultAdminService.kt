package com.stockcomp.service.admin

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.dto.contest.ContestDto
import com.stockcomp.dto.user.UserDetailsDto
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.UserRepository
import com.stockcomp.request.CreateContestRequest
import com.stockcomp.tasks.ContestTasks
import com.stockcomp.util.toContestDto
import com.stockcomp.util.toUserDetailsDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultAdminService(
    private val contestTasks: ContestTasks,
    private val contestRepository: ContestRepository,
    private val userRepository: UserRepository,
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


    override fun updateContestStatus(contestDto: ContestDto): ContestDto =
        contestRepository.findById(contestDto.id).get()
            .let {
                when (it.contestStatus) {
                    ContestStatus.COMPLETED -> {
                        contestTasks.completeContestTasks(contestDto.contestNumber)
                    }
                    ContestStatus.STOPPED -> {
                        contestTasks.stopOrderProcessing()
                        contestTasks.stopMaintainInvestments()
                    }
                    ContestStatus.RUNNING -> {
                        contestTasks.startOrderProcessing()
                        contestTasks.startMaintainInvestments()
                    }
                    ContestStatus.AWAITING_START -> pass
                    else -> pass
                }
                it.contestStatus = contestDto.contestStatus
                contestRepository.save(it).toContestDto()
            }

    override fun getUsers(): List<UserDetailsDto> =
        userRepository.findAll()
            .map { it.toUserDetailsDto() }
}