package com.stockcomp.investmentorder.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.investmentorder.entity.InvestmentOrder
import com.stockcomp.participant.entity.Participant
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.domain.contest.enums.OrderStatus
import com.stockcomp.exception.InvalidStateException
import com.stockcomp.contest.repository.ContestRepository
import com.stockcomp.investmentorder.repository.InvestmentOrderRepository
import com.stockcomp.participant.repository.ParticipantRepository
import com.stockcomp.investmentorder.dto.InvestmentOrderRequest
import com.stockcomp.util.mapToInvestmentOrder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultInvestmentOrderService(
    private val investmentOrderRepository: InvestmentOrderRepository,
    private val participantRepository: ParticipantRepository,
    private val contestRepository: ContestRepository
) : InvestmentOrderService {


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
        investmentOrderRepository.findAllByContestAndOrderStatus(contest, OrderStatus.ACTIVE)
            .onEach { it.orderStatus = OrderStatus.TERMINATED }
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