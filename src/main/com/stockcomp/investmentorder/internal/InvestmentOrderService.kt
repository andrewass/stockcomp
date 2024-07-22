package com.stockcomp.investmentorder.internal

import com.stockcomp.investmentorder.OrderStatus
import com.stockcomp.investmentorder.TransactionType
import com.stockcomp.participant.ParticipantService
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
        contestNumber: Int,
        currency: String,
        acceptedPrice: Double,
        symbol: String,
        expirationTime: LocalDateTime,
        email: String,
        amount: Int,
        transactionType: TransactionType
    ) {
        val participant = participantService.getParticipant(contestNumber, email)!!
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
    fun deleteInvestmentOrder(email: String, orderId: Long, contestNumber: Int) {
        participantService.getParticipant(contestNumber, email)!!
            .also { it.removeInvestmentOrder(orderId) }
            .also { participantService.saveParticipant(it) }
    }

    fun getActiveOrders(contestNumber: Int, email: String): List<InvestmentOrder> =
        participantService.getParticipant(contestNumber, email)
            ?.let { investmentOrderRepository.findAllByParticipantAndOrderStatus(it, OrderStatus.ACTIVE) }
            ?: emptyList()

    fun getCompletedOrders(contestNumber: Int, email: String): List<InvestmentOrder> =
        participantService.getParticipant(contestNumber, email)
            ?.let { investmentOrderRepository.findAllByParticipantAndOrderStatus(it, OrderStatus.COMPLETED) }
            ?: emptyList()

    fun getActiveOrdersSymbol(symbol: String, contestNumber: Int, email: String): List<InvestmentOrder> =
        getSymbolOrdersByStatus(symbol, contestNumber, email)

    fun getCompletedOrdersSymbol(symbol: String, contestNumber: Int, email: String): List<InvestmentOrder> =
        getSymbolOrdersByStatus(symbol, contestNumber, email)

    private fun getSymbolOrdersByStatus(
        symbol: String, contestNumber: Int, email: String
    ): List<InvestmentOrder> =
        participantService.getParticipant(contestNumber, email)
            ?.let {
                investmentOrderRepository.findAllByParticipantAndSymbolAndOrderStatus(
                    it, symbol, OrderStatus.COMPLETED
                )
            } ?: emptyList()
}