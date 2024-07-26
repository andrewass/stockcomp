package com.stockcomp.participant.investment

import com.stockcomp.common.entity.BaseEntity
import com.stockcomp.participant.participant.Participant
import jakarta.persistence.*

@Entity
@Table(name = "T_INVESTMENT")
class Investment(

    @Id
    @Column(name = "INVESTMENT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val symbol: String,

    @ManyToOne
    @JoinColumn(name = "PARTICIPANT_ID", nullable = false)
    val participant: Participant,

    var amount: Int = 0,

    var averageUnitCost: Double = 0.00,

    var totalProfit: Double = 0.00,

    var totalValue: Double = 0.00

) : BaseEntity() {

    fun updateValues(updatedPrice: Double) {
        val newTotalValueInvestment = amount * updatedPrice
        totalValue = newTotalValueInvestment
        totalProfit = newTotalValueInvestment - (amount * averageUnitCost)
    }
}