package com.stockcomp.participant.investmentorder

import com.stockcomp.participant.ParticipantRepository
import com.stockcomp.participant.participant.ParticipantService
import com.stockcomp.symbol.SymbolServiceExternal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class InvestmentOrderProcessingService(
    private val symbolService: SymbolServiceExternal,
    private val participantService: ParticipantService,
    private val participantRepository: ParticipantRepository,
) {

    @Transactional
    fun processInvestmentOrders(participantId: Long) {
        val participant = participantService.getLockedParticipant(participantId)
        participant.getActiveInvestmentOrders()
            .forEach {
                val currentPrice = symbolService.getCurrentPrice(it.symbol)
                it.processOrder(currentPrice.currentPrice)
                participantRepository.save(participant)
            }
    }

    @Transactional
    fun placeInvestmentOrder(
        participantId: Long,
        currency: String,
        acceptedPrice: Double,
        symbol: String,
        expirationTime: LocalDateTime,
        amount: Int,
        transactionType: TransactionType,
    ) {
        val participant = participantRepository.findByParticipantId(participantId)
        InvestmentOrder(
            participant = participant,
            currency = currency,
            acceptedPrice = acceptedPrice,
            expirationTime = expirationTime,
            symbol = symbol,
            totalAmount = amount,
            transactionType = transactionType
        ).also { participant.addInvestmentOrder(it) }
    }

    @Transactional
    fun deleteInvestmentOrder(userId: Long, orderId: Long, contestId: Long) {
        participantRepository.findByUserIdAndContestId(contestId = contestId, userId = userId)!!
            .also { it.removeInvestmentOrder(orderId) }
    }

    fun getActiveOrders(contestId: Long, userId: Long): List<InvestmentOrder> =
        participantRepository.findByUserIdAndContestId(contestId = contestId, userId = userId)
            ?.getActiveInvestmentOrders() ?: emptyList()

    fun getCompletedOrders(contestId: Long, userId: Long): List<InvestmentOrder> =
        participantRepository.findByUserIdAndContestId(contestId = contestId, userId = userId)
            ?.getCompletedInvestmentOrders() ?: emptyList()

    fun getActiveOrdersSymbol(symbol: String, contestId: Long, userId: Long): List<InvestmentOrder> =
        participantRepository.findByUserIdAndContestId(contestId = contestId, userId = userId)
            ?.getActiveInvestmentOrders()
            ?.filter { it.symbol == symbol }
            ?: emptyList()

    fun getCompletedOrdersSymbol(symbol: String, contestId: Long, userId: Long): List<InvestmentOrder> =
        participantRepository.findByUserIdAndContestId(contestId = contestId, userId = userId)
            ?.getCompletedInvestmentOrders()
            ?.filter { it.symbol == symbol }
            ?: emptyList()
}