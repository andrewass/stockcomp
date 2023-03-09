package com.stockcomp.investmentorder.repository

import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.investmentorder.entity.InvestmentOrder
import com.stockcomp.investmentorder.entity.OrderStatus
import com.stockcomp.participant.entity.Participant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface InvestmentOrderRepository : JpaRepository<InvestmentOrder, Long> {

    fun findAllByParticipantAndSymbolAndOrderStatusIn(
        participant: Participant, symbol: String, orderStatus: List<OrderStatus>
    ): List<InvestmentOrder>

    @Query(
        "select io from InvestmentOrder io join io.participant p join p.contest c " +
                "where io.orderStatus = ?1 and c.contestStatus  = ?2"
    )
    fun findAllByOrderAndContestStatus(orderStatus: OrderStatus, contestStatus: ContestStatus): List<InvestmentOrder>

    @Query(
        "select io from InvestmentOrder io join io.participant p join p.contest c " +
                "where c = ?1 and io.orderStatus = ?2"
    )
    fun findAllByContestAndOrderStatus(contest: Contest, orderStatus: OrderStatus): List<InvestmentOrder>
}