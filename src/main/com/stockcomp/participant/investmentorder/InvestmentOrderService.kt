package com.stockcomp.participant.investmentorder

import com.stockcomp.participant.participant.ParticipantService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class InvestmentOrderService(
    private val investmentOrderRepository: InvestmentOrderRepository,
    private val participantService: ParticipantService,
) {

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
        val participant = participantService.getParticipant(participantId)
        InvestmentOrder(
            participant = participant,
            currency = currency,
            acceptedPrice = acceptedPrice,
            expirationTime = expirationTime,
            symbol = symbol,
            totalAmount = amount,
            transactionType = transactionType
        ).also { participant.addInvestmentOrder(it) }
        participantService.saveParticipant(participant)
    }

    @Transactional
    fun deleteInvestmentOrder(userId: Long, orderId: Long, contestId: Long) {
        participantService.getParticipant(contestId = contestId, userId = userId)
            .also { it.removeInvestmentOrder(orderId) }
            .also { participantService.saveParticipant(it) }
    }

    fun getActiveOrders(contestId: Long, userId: Long): List<InvestmentOrder> =
        participantService.getParticipant(contestId = contestId, userId = userId)
            .let { investmentOrderRepository.findAllByParticipantAndOrderStatus(it, OrderStatus.ACTIVE) }

    fun getCompletedOrders(contestId: Long, userId: Long): List<InvestmentOrder> =
        participantService.getParticipant(contestId = contestId, userId = userId)
            .let { investmentOrderRepository.findAllByParticipantAndOrderStatus(it, OrderStatus.COMPLETED) }

    fun getActiveOrdersSymbol(symbol: String, contestId: Long, userId: Long): List<InvestmentOrder> =
        participantService.getParticipant(contestId = contestId, userId = userId)
            .let {
                investmentOrderRepository.findAllByParticipantAndSymbolAndOrderStatus(
                    it, symbol, OrderStatus.ACTIVE
                )
            }

    fun getCompletedOrdersSymbol(symbol: String, contestId: Long, userId: Long): List<InvestmentOrder> =
        participantService.getParticipant(contestId = contestId, userId = userId)
            .let {
                investmentOrderRepository.findAllByParticipantAndSymbolAndOrderStatus(
                    it, symbol, OrderStatus.COMPLETED
                )
            }
}