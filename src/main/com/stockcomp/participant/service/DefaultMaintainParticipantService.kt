package com.stockcomp.participant.service

import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.service.ContestService
import com.stockcomp.participant.repository.ParticipantRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultMaintainParticipantService(
    private val investmentService: InvestmentService,
    private val participantRepository: ParticipantRepository,
    private val contestService: ContestService
) : MaintainParticipantService {

    private val logger = LoggerFactory.getLogger(DefaultMaintainParticipantService::class.java)

    override fun maintainParticipants() {
        try {
            investmentService.updateInvestments()
            updateParticipants()
            updateRanking()
        } catch (e: Exception) {
            logger.error("Failed return maintenance : ${e.message}")
        }
    }


    private fun updateParticipants() {
        participantRepository.findAllByContestStatus(ContestStatus.RUNNING)
            .onEach { participant -> participant.updateValues() }
            .also { participantRepository.saveAll(it) }
    }

    private fun updateRanking() {
        var rankCounter = 1
        participantRepository.findAllByContestOrderByTotalValueDesc(
            contestService.getContests(listOf(ContestStatus.RUNNING)).first()
        ).onEach { it.rank = rankCounter++ }
            .also { participantRepository.saveAll(it) }
    }
}