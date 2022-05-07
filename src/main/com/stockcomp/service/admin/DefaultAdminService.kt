package com.stockcomp.service.admin

import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.dto.ContestDto
import com.stockcomp.dto.user.UserDetailsDto
import com.stockcomp.contest.repository.ContestRepository
import com.stockcomp.repository.UserRepository
import com.stockcomp.contest.dto.CreateContestRequest
import com.stockcomp.contest.dto.UpdateContestRequest
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


    override fun updateContestStatus(request: UpdateContestRequest): ContestDto =
        contestRepository.findByContestNumber(request.contestNumber)
            .let {
                when (it.contestStatus) {
                    ContestStatus.COMPLETED -> {
                        contestTasks.completeContestTasks(request.contestNumber)
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
                }
                it.contestStatus = request.contestStatus
                contestRepository.save(it).toContestDto()
            }

    override fun getUsers(): List<UserDetailsDto> =
        userRepository.findAll()
            .map { it.toUserDetailsDto() }
}