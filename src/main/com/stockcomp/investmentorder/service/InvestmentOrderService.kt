package com.stockcomp.investmentorder.service

import com.stockcomp.investmentorder.dto.PlaceInvestmentOrderRequest
import com.stockcomp.investmentorder.entity.InvestmentOrder
import com.stockcomp.investmentorder.entity.OrderStatus
import com.stockcomp.investmentorder.repository.InvestmentOrderRepository
import com.stockcomp.participant.ParticipantService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InvestmentOrderService(
    private val investmentOrderRepository: InvestmentOrderRepository,
    private val participantService: ParticipantService,
) {

    @Transactional
    fun placeInvestmentOrder(request: PlaceInvestmentOrderRequest, email: String) {
        val participant = participantService.getParticipant(request.contestNumber, email)!!
        InvestmentOrder(
            participant = participant,
            currency = request.currency,
            acceptedPrice = request.acceptedPrice,
            expirationTime = request.expirationTime,
            symbol = request.symbol,
            totalAmount = request.amount,
            transactionType = request.transactionType
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