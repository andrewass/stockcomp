package com.stockcomp.contest.service

import com.stockcomp.contest.dto.CreateContestRequest
import com.stockcomp.contest.dto.UpdateContestRequest
import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.repository.ContestRepository
import com.stockcomp.contest.tasks.ContestTasks
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultContestService(
    private val contestRepository: ContestRepository,
    private val contestTasks: ContestTasks
) : ContestService {

    private val logger = LoggerFactory.getLogger(DefaultContestService::class.java)

    override fun startContest(contestNumber: Int) {
        contestRepository.findByContestNumberAndContestStatus(contestNumber, ContestStatus.AWAITING_START)?.also {
            it.contestStatus = ContestStatus.RUNNING
            contestRepository.save(it)
            contestTasks.startOrderProcessing()
            contestTasks.startMaintainInvestments()
            logger.info("Starting contest $contestNumber")
        } ?: throw NoSuchElementException("Unable to start contest. Contest with number $contestNumber not found")
    }

    override fun stopContest(contestNumber: Int) {
        contestRepository.findByContestNumberAndContestStatus(contestNumber, ContestStatus.RUNNING)
            ?.also {
                it.contestStatus = ContestStatus.STOPPED
                contestRepository.save(it)
                contestTasks.stopOrderProcessing()
                contestTasks.stopMaintainInvestments()
                logger.info("Stopping contest $contestNumber")
            }
            ?: throw NoSuchElementException("Contest with number $contestNumber not found, or without expected status")
    }

    override fun completeContest(contestNumber: Int) {
        contestRepository.findByContestNumber(contestNumber)
            ?.takeIf { contest -> contest.contestStatus in listOf(ContestStatus.RUNNING, ContestStatus.STOPPED) }
            ?.also {
                it.contestStatus = ContestStatus.COMPLETED
                contestRepository.save(it)
                contestTasks.stopOrderProcessing()
                contestTasks.stopMaintainInvestments()
                logger.info("Completing contest $contestNumber")
            }
            ?: throw NoSuchElementException("Contest with number $contestNumber not found, or without expected status")
    }

    override fun createContest(request: CreateContestRequest) {
        Contest(
            contestNumber = request.contestNumber,
            startTime = request.startTime,
            endTime = request.startTime.plusMonths(2)
        ).also { contestRepository.save(it) }
    }

    override fun getContest(contestNumber: Int): Contest =
        contestRepository.findByContestNumber(contestNumber)


    override fun deleteContest(contestNumber: Int) {
        contestRepository.deleteByContestNumber(contestNumber)
    }

    override fun updateContest(request: UpdateContestRequest) {
        contestRepository.findByContestNumber(request.contestNumber).apply {
            contestStatus = request.contestStatus
            startTime = request.startTime
            endTime = request.startTime.plusMonths(2)
        }.also { contestRepository.save(it) }
    }

    override fun getContests(statusList: List<ContestStatus>): List<Contest> =
        if (statusList.isEmpty()) {
            contestRepository.findAll()
        } else {
            contestRepository.findAllByContestStatusList(statusList)
        }

    override fun findByContestNumber(contestNumber: Int): Contest =
        contestRepository.findByContestNumber(contestNumber)


    override fun saveContest(contest: Contest) {
        contestRepository.save(contest)
    }
}