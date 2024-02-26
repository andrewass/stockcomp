package com.stockcomp.investmentorder.service

import com.stockcomp.contest.repository.ContestRepository
import com.stockcomp.investmentorder.dto.PlaceInvestmentOrderRequest
import com.stockcomp.investmentorder.entity.InvestmentOrder
import com.stockcomp.investmentorder.entity.OrderStatus
import com.stockcomp.investmentorder.repository.InvestmentOrderRepository
import com.stockcomp.participant.service.ParticipantService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultInvestmentOrderService(
    private val investmentOrderRepository: InvestmentOrderRepository,
    private val participantService: ParticipantService,
    private val contestRepository: ContestRepository
) : InvestmentOrderService {

    @Transactional
    override fun placeInvestmentOrder(request: PlaceInvestmentOrderRequest, email: String) {
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
    override fun deleteInvestmentOrder(email: String, orderId: Long, contestNumber: Int): Long {
        participantService.getParticipant(contestNumber, email)!!
            .also { it.removeInvestmentOrder(orderId) }
            .also { participantService.saveParticipant(it) }
        return orderId
    }

    @Transactional(readOnly = true)
    override fun getAllOrdersByStatus(statusList: List<OrderStatus>, email: String): List<InvestmentOrder> =
        participantService.getActiveParticipantsByUser(email)
            .flatMap { it.investmentOrders }
            .filter { statusList.contains(it.orderStatus) }

    override fun getSymbolOrdersByStatus(
        contestNumber: Int, symbol: String,
        statusList: List<OrderStatus>, email: String
    ): List<InvestmentOrder> =
        contestRepository.findByContestNumber(contestNumber)
            .let { participantService.getAllByEmailAndContest(email, it).first() }
            .let { investmentOrderRepository.findAllByParticipantAndSymbolAndOrderStatusIn(it, symbol, statusList) }
}