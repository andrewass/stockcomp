package com.stockcomp.participant.service

import com.stockcomp.contest.repository.ContestRepository
import com.stockcomp.participant.dto.GetInvestmentBySymbolRequest
import com.stockcomp.participant.dto.InvestmentDto
import com.stockcomp.participant.dto.mapToInvestmentDto
import com.stockcomp.participant.entity.Participant
import com.stockcomp.participant.repository.InvestmentRepository
import com.stockcomp.participant.repository.ParticipantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultInvestmentService(
    private val investmentRepository: InvestmentRepository,
    private val contestRepository: ContestRepository,
    private val participantRepository: ParticipantRepository,
) : InvestmentService {

    override fun getInvestmentForSymbol(username: String, request: GetInvestmentBySymbolRequest):
            InvestmentDto? =
        getParticipant(username, request.contestNumber)
            .investments.firstOrNull { it.symbol == request.symbol }
            ?.let { mapToInvestmentDto(it) }


    override fun getAllInvestmentsForParticipant(username: String, contestNumber: Int): List<InvestmentDto> =
        getParticipant(username, contestNumber)
            .let { investmentRepository.findAllByParticipant(it) }
            .map { mapToInvestmentDto(it) }


    private fun getParticipant(username: String, contestNumber: Int): Participant =
        contestRepository.findByContestNumber(contestNumber)
            .let { participantRepository.findAllByUsernameAndContest(username, it) }
            .first()
}