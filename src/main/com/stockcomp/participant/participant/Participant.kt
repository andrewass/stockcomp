package com.stockcomp.participant.participant

import com.stockcomp.common.entity.BaseEntity
import com.stockcomp.participant.investment.Investment
import com.stockcomp.participant.investmentorder.InvestmentOrder
import jakarta.persistence.*

@Entity
@Table(name = "T_PARTICIPANT")
class Participant(

    @Id
    @Column(name = "PARTICIPANT_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val participantId: Long? = null,

    @Column(name = "USER_ID", nullable = false)
    val userId: Long,

    @OneToMany(mappedBy = "participant", cascade = [CascadeType.ALL])
    val investmentOrders: MutableList<InvestmentOrder> = mutableListOf(),

    @OneToMany(mappedBy = "participant", cascade = [CascadeType.ALL])
    val investments: MutableList<Investment> = mutableListOf(),

    @Column(name = "CONTEST_ID", nullable = false)
    val contestId: Long,

    var remainingFunds: Double = 20000.00,

    @Column(name = "PARTICIPANT_RANK")
    var rank: Int? = null,

    var totalValue: Double = 20000.00,

    var totalInvestmentValue: Double = 0.00

) : BaseEntity() {

    fun updateInvestmentValues() {
        val updatedTotalInvestmentsValue = investments.sumOf { it.totalValue }
        totalInvestmentValue = updatedTotalInvestmentsValue
        totalValue = remainingFunds + updatedTotalInvestmentsValue
    }

    fun addInvestmentOrder(investmentOrder: InvestmentOrder) {
        investmentOrders.add(investmentOrder)
    }

    fun removeInvestmentOrder(orderId: Long) {
        investmentOrders.removeIf { it.orderId == orderId }
    }

    fun addInvestment(investment: Investment) {
        investments.add(investment)
    }

    fun removeInvestment(investment: Investment) {
        investments.remove(investment)
    }
}