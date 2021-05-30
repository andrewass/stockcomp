package com.stockcomp.service.order

import com.stockcomp.domain.contest.OrderStatus
import com.stockcomp.repository.jpa.ContestRepository
import com.stockcomp.repository.jpa.InvestmentOrderRepository
import com.stockcomp.repository.jpa.ParticipantRepository
import com.stockcomp.response.InvestmentOrderDto
import com.stockcomp.service.util.mapToInvestmentOrderDto
import org.springframework.stereotype.Service

@Service
class DefaultInvestmentOrderService(
    private val investmentOrderRepository: InvestmentOrderRepository,
    private val participantRepository: ParticipantRepository,
    private val contestRepository: ContestRepository
) : InvestmentOrderService {

    override fun getAllCompletedOrdersForParticipant(username: String, contestNumber: Int)
            : List<InvestmentOrderDto> {
        return findOrdersByParticipant(username, contestNumber, listOf(OrderStatus.COMPLETED, OrderStatus.FAILED))
    }

    override fun getAllCompletedOrdersForSymbolForParticipant(username: String, symbol: String, contestNumber: Int)
            : List<InvestmentOrderDto> {
        val contest = contestRepository.findContestByContestNumber(contestNumber).get()
        val participant = participantRepository.findParticipantFromUsernameAndContest(username, contest).first()

        val investmentOrders = investmentOrderRepository.findAllByParticipantAndSymbolAndOrderStatusIn(
            participant, symbol, listOf(OrderStatus.ACTIVE, OrderStatus.FAILED)
        )
        return investmentOrders.map { mapToInvestmentOrderDto(it) }
    }

    override fun getAllActiveOrdersForParticipant(username: String, contestNumber: Int): List<InvestmentOrderDto> {
        TODO("Not yet implemented")
    }

    override fun getAllActiveOrdersForSymbolForParticipant(username: String, symbol: String, contestNumber: Int)
            : List<InvestmentOrderDto> {
        TODO("Not yet implemented")
    }

    override fun deleteActiveInvestmentOrder(username: String, orderId: Long) {
        TODO("Not yet implemented")
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
            participant, symbol, listOf(OrderStatus.ACTIVE, OrderStatus.FAILED)
        )
        return investmentOrders.map { mapToInvestmentOrderDto(it) }
    }
}