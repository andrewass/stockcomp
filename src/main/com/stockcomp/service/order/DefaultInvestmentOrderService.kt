package com.stockcomp.service.order

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.domain.contest.enums.OrderStatus
import com.stockcomp.domain.contest.enums.OrderStatus.*
import com.stockcomp.domain.contest.enums.TransactionType
import com.stockcomp.dto.InvestmentOrderDto
import com.stockcomp.exception.InvalidStateException
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.InvestmentOrderRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.request.InvestmentOrderRequest
import com.stockcomp.util.mapToInvestmentOrder
import com.stockcomp.util.mapToInvestmentOrderDto
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultInvestmentOrderService(
    private val investmentOrderRepository: InvestmentOrderRepository,
    private val participantRepository: ParticipantRepository,
    private val contestRepository: ContestRepository,
    meterRegistry: SimpleMeterRegistry
) : InvestmentOrderService {

    private val sellOrderCounter = Counter.builder("sell.order.placed")
        .description("Number of sell orders placed")
        .register(meterRegistry)

    private val buyOrderCounter = Counter.builder("buy.order.placed")
        .description("Number of buy orders placed")
        .register(meterRegistry)


    override fun placeBuyOrder(investmentRequest: InvestmentOrderRequest, username: String) {
        buyOrderCounter.increment()
        mapToInvestmentOrder(
            getParticipant(username, investmentRequest.contestNumber),
            investmentRequest,
            TransactionType.BUY
        ).also { investmentOrderRepository.save(it) }
    }

    override fun placeSellOrder(investmentRequest: InvestmentOrderRequest, username: String) {
        sellOrderCounter.increment()
        mapToInvestmentOrder(
            getParticipant(username, investmentRequest.contestNumber),
            investmentRequest,
            TransactionType.SELL
        ).also { investmentOrderRepository.save(it) }
    }

    override fun deleteActiveInvestmentOrder(username: String, orderId: Long) {
        investmentOrderRepository.findById(orderId).get()
            .takeIf { it.participant.user.username == username }
            ?.also { investmentOrderRepository.delete(it) }
            ?: throw InvalidStateException("Attempting to delete order not tied to user : $orderId")
    }

    override fun getAllCompletedOrdersForParticipant(username: String, contestNumber: Int): List<InvestmentOrderDto> =
        findOrdersByParticipant(username, contestNumber, listOf(COMPLETED, FAILED))


    override fun getAllCompletedOrdersForSymbolForParticipant(username: String, symbol: String, contestNumber: Int)
            : List<InvestmentOrderDto> =
        findOrdersByParticipantAndSymbol(username, contestNumber, symbol, listOf(COMPLETED, FAILED))


    override fun getAllActiveOrdersForParticipant(username: String, contestNumber: Int): List<InvestmentOrderDto> =
        findOrdersByParticipant(username, contestNumber, listOf(ACTIVE))


    override fun getAllActiveOrdersForSymbolForParticipant(username: String, symbol: String, contestNumber: Int)
            : List<InvestmentOrderDto> =
        findOrdersByParticipantAndSymbol(username, contestNumber, symbol, listOf(ACTIVE))

    override fun terminateRemainingOrders(contest: Contest) {
        TODO("Not yet implemented")
    }


    private fun findOrdersByParticipant(
        username: String, contestNumber: Int, orderStatus: List<OrderStatus>
    ): List<InvestmentOrderDto> =
        contestRepository.findByContestNumber(contestNumber)
            .let { participantRepository.findParticipantFromUsernameAndContest(username, it).first() }
            .let { investmentOrderRepository.findAllByParticipantAndOrderStatusIn(it, orderStatus) }
            .let { it.map { order -> mapToInvestmentOrderDto(order) } }


    private fun findOrdersByParticipantAndSymbol(
        username: String, contestNumber: Int, symbol: String, orderStatus: List<OrderStatus>
    ): List<InvestmentOrderDto> =
        contestRepository.findByContestNumber(contestNumber)
            .let { participantRepository.findParticipantFromUsernameAndContest(username, it).first() }
            .let {
                investmentOrderRepository.findAllByParticipantAndSymbolAndOrderStatusIn(
                    it, symbol, orderStatus
                )
            }.let { it.map { order -> mapToInvestmentOrderDto(order) } }


    private fun getParticipant(username: String, contestNumber: Int): Participant =
        contestRepository.findByContestNumberAndContestStatus(contestNumber, ContestStatus.RUNNING)
            .let { participantRepository.findParticipantFromUsernameAndContest(username, it) }.first()
}