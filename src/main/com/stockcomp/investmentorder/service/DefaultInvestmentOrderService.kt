package com.stockcomp.investmentorder.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.repository.ContestRepository
import com.stockcomp.exception.InvalidStateException
import com.stockcomp.investmentorder.dto.GetInvestmentOrderRequest
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

    override fun placeInvestmentOrder(request: PlaceInvestmentOrderRequest) {
        InvestmentOrder(
            participant = getParticipant(request.ident, request.contestNumber),
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

    override fun getOrdersByStatus(request: GetInvestmentOrderRequest): List<InvestmentOrder> =
        findOrdersByParticipant(request.ident, request.contestNumber, request.statusList)


    override fun getSymbolOrdersByStatus(request: GetInvestmentOrderRequest): List<InvestmentOrder> =
        findOrdersByParticipantAndSymbol(request.ident, request.contestNumber, request.symbol!!, request.statusList)


    override fun terminateRemainingOrders(contest: Contest) {
        investmentOrderRepository.findAllByContestAndOrderStatus(contest, OrderStatus.ACTIVE)
            .onEach { it.orderStatus = OrderStatus.TERMINATED }
            .also { investmentOrderRepository.saveAll(it) }
    }

    private fun findOrdersByParticipant(
        username: String, contestNumber: Int, orderStatus: List<OrderStatus>
    ): List<InvestmentOrder> =
        contestRepository.findByContestNumber(contestNumber)
            .let { participantService.getAllByUsernameAndContest(username, it).first() }
            .let { investmentOrderRepository.findAllByParticipantAndOrderStatusIn(it, orderStatus) }


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