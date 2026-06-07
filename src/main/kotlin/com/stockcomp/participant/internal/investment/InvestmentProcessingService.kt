package com.stockcomp.participant.internal.investment

import com.stockcomp.participant.internal.ParticipantRepository
import com.stockcomp.symbol.SymbolServiceExternal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class InvestmentProcessingService(
    private val participantRepository: ParticipantRepository,
    private val investmentProcessingTransactions: InvestmentProcessingTransactions,
    private val symbolService: SymbolServiceExternal,
) {
    fun maintainInvestments(participantId: Long) {
        val symbols = investmentProcessingTransactions.getInvestmentSymbols(participantId)
        if (symbols.isEmpty()) {
            return
        }

        val pricesBySymbol =
            symbols.associateWith { symbol ->
                symbolService.getCurrentPrice(symbol).currentPrice
            }
        investmentProcessingTransactions.maintainInvestments(participantId, pricesBySymbol)
    }

    @Transactional(readOnly = true)
    fun getInvestmentForSymbol(
        contestId: Long,
        userId: Long,
        symbol: String,
    ): Investment? {
        val normalizedSymbol = symbol.trim().uppercase()
        return participantRepository
            .findByUserIdAndContestId(contestId = contestId, userId = userId)
            ?.investments()
            ?.firstOrNull { it.symbol == normalizedSymbol }
    }

    @Transactional(readOnly = true)
    fun getInvestmentsForParticipant(
        contestId: Long,
        userId: Long,
    ): List<Investment> =
        participantRepository
            .findByUserIdAndContestId(contestId = contestId, userId = userId)
            ?.investments() ?: emptyList()
}

@Service
class InvestmentProcessingTransactions(
    private val participantRepository: ParticipantRepository,
) {
    @Transactional(readOnly = true)
    fun getInvestmentSymbols(participantId: Long): Set<String> =
        participantRepository
            .findByParticipantId(participantId)
            ?.investments()
            ?.map { it.symbol }
            ?.toSet() ?: emptySet()

    @Transactional
    fun maintainInvestments(
        participantId: Long,
        pricesBySymbol: Map<String, BigDecimal>,
    ) {
        val participant = participantRepository.findByIdLocked(participantId)
        participant
            .investments()
            .forEach { investment ->
                pricesBySymbol[investment.symbol]?.let { price ->
                    investment.maintainInvestment(price)
                }
            }
        participant.updateInvestmentValues()
        participantRepository.save(participant)
    }
}
