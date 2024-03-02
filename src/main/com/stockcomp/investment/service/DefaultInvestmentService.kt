package com.stockcomp.investment.service

import com.stockcomp.investment.entity.Investment
import com.stockcomp.investment.repository.InvestmentRepository
import com.stockcomp.participant.ParticipantService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultInvestmentService(
    private val investmentRepository: InvestmentRepository,
    private val participantService: ParticipantService
) : InvestmentService {

    override fun getInvestmentForSymbol(contestNumber: Int, ident: String, symbol: String): Investment? =
        participantService.getParticipant(contestNumber, ident)!!
            .investments.firstOrNull { it.symbol == symbol }


    override fun getAllInvestmentsForParticipant(ident: String): List<Investment> =
        participantService.getActiveParticipantsByUser(ident)
            .flatMap { investmentRepository.findAllByParticipant(it) }
}