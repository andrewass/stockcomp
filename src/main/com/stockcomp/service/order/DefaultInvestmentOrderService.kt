package com.stockcomp.service.order

import com.stockcomp.domain.contest.Contest
import com.stockcomp.domain.contest.InvestmentOrder
import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.domain.contest.enums.OrderStatus
import com.stockcomp.domain.contest.enums.OrderStatus.ACTIVE
import com.stockcomp.domain.contest.enums.OrderStatus.TERMINATED
import com.stockcomp.exception.InvalidStateException
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.InvestmentOrderRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.request.InvestmentOrderRequest
import com.stockcomp.util.mapToInvestmentOrder
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


    override fun placeInvestmentOrder(investmentRequest: InvestmentOrderRequest, username: String): Long =
        mapToInvestmentOrder(getParticipant(username, investmentRequest.contestNumber), investmentRequest)
            .let { investmentOrderRepository.save(it) }.orderId!!


    override fun deleteInvestmentOrder(username: String, orderId: Long): Long {
        investmentOrderRepository.findById(orderId).get()
            .takeIf { it.participant.user.username == username }
            ?.also { investmentOrderRepository.delete(it) }
            ?: throw InvalidStateException("Attempting to delete order not tied to user : $orderId")

        return orderId
    }

    override fun getOrdersByStatus(
        username: String, contestNumber: Int, status: List<OrderStatus>
    ): List<InvestmentOrder> =
        findOrdersByParticipant(username, contestNumber, status)


    override fun getSymbolOrdersByStatus(
        username: String, contestNumber: Int,
        status: List<OrderStatus>, symbol: String
    ): List<InvestmentOrder> =
        findOrdersByParticipantAndSymbol(username, contestNumber, symbol, status)


    override fun terminateRemainingOrders(contest: Contest) {
        investmentOrderRepository.findAllByContestAndOrderStatus(contest, ACTIVE)
            .onEach { it.orderStatus = TERMINATED }
            .also { investmentOrderRepository.saveAll(it) }
    }

    private fun findOrdersByParticipant(
        username: String, contestNumber: Int, orderStatus: List<OrderStatus>
    ): List<InvestmentOrder> =
        contestRepository.findByContestNumber(contestNumber)
            .let { participantRepository.findAllByUsernameAndContest(username, it).first() }
            .let { investmentOrderRepository.findAllByParticipantAndOrderStatusIn(it, orderStatus) }


    private fun findOrdersByParticipantAndSymbol(
        username: String, contestNumber: Int, symbol: String, orderStatus: List<OrderStatus>
    ): List<InvestmentOrder> =
        contestRepository.findByContestNumber(contestNumber)
            .let { participantRepository.findAllByUsernameAndContest(username, it).first() }
            .let { investmentOrderRepository.findAllByParticipantAndSymbolAndOrderStatusIn(it, symbol, orderStatus) }


    private fun getParticipant(username: String, contestNumber: Int): Participant =
        contestRepository.findByContestNumberAndContestStatus(contestNumber, ContestStatus.RUNNING)
            .let { participantRepository.findAllByUsernameAndContest(username, it) }.first()
}