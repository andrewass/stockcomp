package com.stockcomp.participant.entity

import com.stockcomp.common.entity.BaseEntity
import com.stockcomp.contest.entity.Contest
import com.stockcomp.investmentorder.entity.InvestmentOrder
import com.stockcomp.user.entity.User
import jakarta.persistence.*

@Entity
@Table(name = "T_PARTICIPANT")
class Participant(

    @Id
    @Column(name = "PARTICIPANT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    val user: User,

    @OneToMany(mappedBy = "participant", cascade = [CascadeType.ALL])
    val investmentOrders: MutableList<InvestmentOrder> = mutableListOf(),

    @OneToMany(mappedBy = "participant", cascade = [CascadeType.ALL])
    val investments: MutableList<Investment> = mutableListOf(),

    @ManyToOne
    @JoinColumn(name = "CONTEST_ID")
    val contest: Contest,

    var remainingFunds: Double = 20000.00,

    @Column(name = "PARTICIPANT_RANK")
    var rank: Int,

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