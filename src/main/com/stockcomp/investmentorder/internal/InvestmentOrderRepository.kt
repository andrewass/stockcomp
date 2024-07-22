package com.stockcomp.investmentorder.internal

import com.stockcomp.investmentorder.OrderStatus
import com.stockcomp.participant.entity.Participant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InvestmentOrderRepository : JpaRepository<InvestmentOrder, Long> {

    fun findAllByParticipantAndSymbolAndOrderStatus(
        participant: Participant, symbol: String, orderStatus: OrderStatus
    ): List<InvestmentOrder>

    fun findAllByParticipantAndOrderStatus(
        participant: Participant, orderStatus: OrderStatus
    ): List<InvestmentOrder>
}