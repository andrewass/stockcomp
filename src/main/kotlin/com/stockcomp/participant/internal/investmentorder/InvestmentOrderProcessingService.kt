package com.stockcomp.participant.internal.investmentorder

import com.stockcomp.participant.internal.ParticipantService
import com.stockcomp.symbol.SymbolServiceExternal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class InvestmentOrderProcessingService(
    private val symbolService: SymbolServiceExternal,
    private val participantService: ParticipantService,
) {
    @Transactional
    fun processInvestmentOrders(participantId: Long) {
        val participant = participantService.getParticipantByIdLocked(participantId)
        participant
            .getActiveInvestmentOrders()
            .forEach {
                val currentPrice = symbolService.getCurrentPrice(it.symbol)
                it.processOrder(currentPrice.currentPrice)
            }
        participant.updateInvestmentValues()
        participantService.saveParticipant(participant)
    }

    @Transactional
    fun placeInvestmentOrder(
        userId: Long,
        participantId: Long,
        currency: String,
        acceptedPrice: Double,
        symbol: String,
        expirationTime: LocalDateTime,
        amount: Int,
        transactionType: TransactionType,
    ) {
        val participant = participantService.getParticipantByIdAndUserId(participantId, userId)
        InvestmentOrder(
            participant = participant,
            currency = currency,
            acceptedPrice = acceptedPrice,
            expirationTime = expirationTime,
            symbol = symbol.trim().uppercase(),
            totalAmount = amount,
            transactionType = transactionType,
        ).also { participant.addInvestmentOrder(it) }
        participantService.saveParticipant(participant)
    }

    @Transactional
    fun deleteInvestmentOrder(
        userId: Long,
        orderId: Long,
        contestId: Long,
    ) {
        val participant = participantService.getParticipant(contestId = contestId, userId = userId)
        participant.removeInvestmentOrder(orderId)
        participantService.saveParticipant(participant)
    }

    fun getActiveOrders(
        contestId: Long,
        userId: Long,
    ): List<InvestmentOrder> =
        participantService
            .findOptionalParticipant(contestId = contestId, userId = userId)
            ?.getActiveInvestmentOrders() ?: emptyList()

    fun getCompletedOrders(
        contestId: Long,
        userId: Long,
    ): List<InvestmentOrder> =
        participantService
            .findOptionalParticipant(contestId = contestId, userId = userId)
            ?.getCompletedInvestmentOrders() ?: emptyList()

    fun getActiveOrdersSymbol(
        symbol: String,
        contestId: Long,
        userId: Long,
    ): List<InvestmentOrder> =
        participantService
            .findOptionalParticipant(contestId = contestId, userId = userId)
            ?.getActiveInvestmentOrdersForSymbol(symbol.trim().uppercase())
            ?: emptyList()

    fun getCompletedOrdersSymbol(
        symbol: String,
        contestId: Long,
        userId: Long,
    ): List<InvestmentOrder> =
        participantService
            .findOptionalParticipant(contestId = contestId, userId = userId)
            ?.getCompletedInvestmentOrdersForSymbol(symbol.trim().uppercase())
            ?: emptyList()
}
