package com.stockcomp.investmentorder.service

import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.repository.ContestRepository
import com.stockcomp.exception.InvalidStateException
import com.stockcomp.investmentorder.dto.PlaceInvestmentOrderRequest
import com.stockcomp.investmentorder.entity.InvestmentOrder
import com.stockcomp.investmentorder.entity.OrderStatus
import com.stockcomp.investmentorder.repository.InvestmentOrderRepository
import com.stockcomp.participant.entity.Participant
import com.stockcomp.participant.service.ParticipantService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultInvestmentOrderService(
    private val investmentOrderRepository: InvestmentOrderRepository,
    private val participantService: ParticipantService,
    private val contestRepository: ContestRepository
) : InvestmentOrderService {

    override fun placeInvestmentOrder(request: PlaceInvestmentOrderRequest, ident: String) {
        InvestmentOrder(
            participant = getParticipant(ident, request.contestNumber),
            currency = request.currency,
            acceptedPrice = request.acceptedPrice,
            expirationTime = request.expirationTime,
            symbol = request.symbol,
            totalAmount = request.amount,
            transactionType = request.transactionType
        ).let { investmentOrderRepository.save(it) }.orderId!!
    }

    override fun deleteInvestmentOrder(email: String, orderId: Long): Long {
        investmentOrderRepository.findById(orderId).get()
            .takeIf { it.participant.user.email == email }
            ?.also { investmentOrderRepository.delete(it) }
            ?: throw InvalidStateException("Attempting to delete order not tied to user : $orderId")

        return orderId
    }

    override fun getAllOrdersByStatus(statusList: List<OrderStatus>, ident: String): List<InvestmentOrder> =
        participantService.getActiveParticipantsByUser(ident)
            .flatMap { it.investmentOrders }
            .filter { statusList.contains(it.orderStatus) }


    override fun getSymbolOrdersByStatus(
        contestNumber: Int, symbol: String,
        statusList: List<OrderStatus>, ident: String
    ): List<InvestmentOrder> =
        findOrdersByParticipantAndSymbol(ident, contestNumber, symbol, statusList)


    private fun findOrdersByParticipantAndSymbol(
        username: String, contestNumber: Int, symbol: String, orderStatus: List<OrderStatus>
    ): List<InvestmentOrder> =
        contestRepository.findByContestNumber(contestNumber)
            .let { participantService.getAllByUsernameAndContest(username, it).first() }
            .let { investmentOrderRepository.findAllByParticipantAndSymbolAndOrderStatusIn(it, symbol, orderStatus) }


    private fun getParticipant(username: String, contestNumber: Int): Participant =
        contestRepository.findByContestNumberAndContestStatus(contestNumber, ContestStatus.RUNNING)
            .let { participantService.getAllByUsernameAndContest(username, it) }.first()
}