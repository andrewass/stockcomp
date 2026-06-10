package com.stockcomp.participant.internal.investmentorder

import com.stockcomp.participant.TransactionType
import com.stockcomp.participant.internal.ParticipantRepository
import com.stockcomp.participant.internal.ParticipantService
import com.stockcomp.symbol.SymbolServiceExternal
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class InvestmentOrderProcessingService(
    private val symbolService: SymbolServiceExternal,
    private val participantService: ParticipantService,
    private val investmentOrderProcessingTransactions: InvestmentOrderProcessingTransactions,
) {
    fun processInvestmentOrders(participantId: Long) {
        val symbols = investmentOrderProcessingTransactions.getActiveInvestmentOrderSymbols(participantId)
        if (symbols.isEmpty()) {
            return
        }

        val pricesBySymbol =
            symbols.associateWith { symbol ->
                symbolService.getCurrentPrice(symbol).currentPrice
            }
        investmentOrderProcessingTransactions.processActiveInvestmentOrders(participantId, pricesBySymbol)
    }

    @Transactional
    fun placeInvestmentOrder(
        userId: Long,
        participantId: Long,
        currency: String,
        acceptedPrice: BigDecimal,
        symbol: String,
        expirationTime: LocalDateTime,
        amount: Int,
        transactionType: TransactionType,
    ) {
        val participant = participantService.getParticipantByIdAndUserIdLocked(participantId, userId)
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
        val participant = participantService.getParticipantLocked(contestId = contestId, userId = userId)
        participant.removeInvestmentOrder(orderId)
        participantService.saveParticipant(participant)
    }

    @Transactional(readOnly = true)
    fun getActiveOrders(
        contestId: Long,
        userId: Long,
    ): List<InvestmentOrder> =
        participantService
            .findOptionalParticipant(contestId = contestId, userId = userId)
            ?.getActiveInvestmentOrders() ?: emptyList()

    @Transactional(readOnly = true)
    fun getCompletedOrders(
        contestId: Long,
        userId: Long,
    ): List<InvestmentOrder> =
        participantService
            .findOptionalParticipant(contestId = contestId, userId = userId)
            ?.getCompletedInvestmentOrders() ?: emptyList()

    @Transactional(readOnly = true)
    fun getActiveOrdersSymbol(
        symbol: String,
        contestId: Long,
        userId: Long,
    ): List<InvestmentOrder> =
        participantService
            .findOptionalParticipant(contestId = contestId, userId = userId)
            ?.getActiveInvestmentOrdersForSymbol(symbol.trim().uppercase())
            ?: emptyList()

    @Transactional(readOnly = true)
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

@Service
class InvestmentOrderProcessingTransactions(
    private val participantRepository: ParticipantRepository,
) {
    @Transactional(readOnly = true)
    fun getActiveInvestmentOrderSymbols(participantId: Long): Set<String> =
        participantRepository
            .findByParticipantId(participantId)
            ?.getActiveInvestmentOrders()
            ?.map { it.symbol }
            ?.toSet() ?: emptySet()

    @Transactional
    fun processActiveInvestmentOrders(
        participantId: Long,
        pricesBySymbol: Map<String, BigDecimal>,
    ) {
        val participant = participantRepository.findByIdLocked(participantId)
        participant
            .getActiveInvestmentOrders()
            .forEach { order ->
                pricesBySymbol[order.symbol]?.let { price ->
                    order.processOrder(price)
                }
            }
        participant.updateInvestmentValues()
        participantRepository.save(participant)
    }
}
