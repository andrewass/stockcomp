package com.stockcomp.service.order

import com.stockcomp.domain.contest.OrderStatus
import com.stockcomp.domain.contest.OrderStatus.*
import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.contest.TransactionType
import com.stockcomp.exception.InvalidStateException
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.InvestmentOrderRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.request.InvestmentOrderRequest
import com.stockcomp.response.InvestmentOrderDto
import com.stockcomp.service.util.mapToInvestmentOrder
import com.stockcomp.service.util.mapToInvestmentOrderDto
import org.springframework.stereotype.Service

@Service
class DefaultInvestmentOrderService(
    private val investmentOrderRepository: InvestmentOrderRepository,
    private val participantRepository: ParticipantRepository,
    private val contestRepository: ContestRepository
) : InvestmentOrderService {

    override fun placeBuyOrder(request: InvestmentOrderRequest, username: String) {
        val participant = getParticipant(username, request.contestNumber)
        val order = mapToInvestmentOrder(participant, request, TransactionType.BUY)
        participant.investmentOrders.add(order)
        participantRepository.save(participant)
    }

    override fun placeSellOrder(request: InvestmentOrderRequest, username: String) {
        val participant = getParticipant(username, request.contestNumber)
        val order = mapToInvestmentOrder(participant, request, TransactionType.SELL)
        participant.investmentOrders.add(order)
        participantRepository.save(participant)
    }


    override fun deleteActiveInvestmentOrder(username: String, orderId: Long) {
        val order = investmentOrderRepository.findById(orderId).get()
        if (order.participant.user.username == username) {
            investmentOrderRepository.delete(order)
        } else {
            throw InvalidStateException("Attempting to delete order not tied to user $orderId")
        }
    }

    override fun getAllCompletedOrdersForParticipant(username: String, contestNumber: Int)
            : List<InvestmentOrderDto> {
        return findOrdersByParticipant(username, contestNumber, listOf(COMPLETED, FAILED))
    }

    override fun getAllCompletedOrdersForSymbolForParticipant(username: String, symbol: String, contestNumber: Int)
            : List<InvestmentOrderDto> {
        return findOrdersByParticipantAndSymbol(username, contestNumber, symbol, listOf(COMPLETED, FAILED))
    }

    override fun getAllActiveOrdersForParticipant(username: String, contestNumber: Int): List<InvestmentOrderDto> {
        return findOrdersByParticipant(username, contestNumber, listOf(ACTIVE))
    }

    override fun getAllActiveOrdersForSymbolForParticipant(username: String, symbol: String, contestNumber: Int)
            : List<InvestmentOrderDto> {
        return findOrdersByParticipantAndSymbol(username, contestNumber, symbol, listOf(ACTIVE))
    }

    private fun findOrdersByParticipant(
        username: String, contestNumber: Int, orderStatus: List<OrderStatus>
    ): List<InvestmentOrderDto> {
        val contest = contestRepository.findContestByContestNumber(contestNumber).get()
        val participant = participantRepository.findParticipantFromUsernameAndContest(username, contest).first()

        val investmentOrders = investmentOrderRepository.findAllByParticipantAndOrderStatusIn(
            participant, orderStatus
        )
        return investmentOrders.map { mapToInvestmentOrderDto(it) }
    }

    private fun findOrdersByParticipantAndSymbol(
        username: String, contestNumber: Int, symbol: String, orderStatus: List<OrderStatus>
    ): List<InvestmentOrderDto> {
        val contest = contestRepository.findContestByContestNumber(contestNumber).get()
        val participant = participantRepository.findParticipantFromUsernameAndContest(username, contest).first()

        val investmentOrders = investmentOrderRepository.findAllByParticipantAndSymbolAndOrderStatusIn(
            participant, symbol, orderStatus
        )
        return investmentOrders.map { mapToInvestmentOrderDto(it) }
    }

    private fun getParticipant(username: String, contestNumber: Int): Participant {
        val contest = contestRepository.findContestByContestNumberAndRunningIsTrue(contestNumber)

        return participantRepository.findParticipantFromUsernameAndContest(
            username, contest.get()
        ).stream().findFirst().get()
    }
}