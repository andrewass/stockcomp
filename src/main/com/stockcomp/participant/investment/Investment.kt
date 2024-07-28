package com.stockcomp.participant.investment

import com.stockcomp.common.BaseEntity
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

    fun updateWhenBuying(amount: Int, currentPrice: Double){
        averageUnitCost = calculateAverageUnitCost(currentPrice = currentPrice, amount = amount)
        this.amount += amount
    }

    fun updateWhenSelling(amount: Int){
        this.amount -= amount
    }

    fun maintainInvestment(updatedPrice: Double) {
        val newTotalValueInvestment = amount * updatedPrice
        totalValue = newTotalValueInvestment
        totalProfit = newTotalValueInvestment - (amount * averageUnitCost)
    }

    private fun calculateAverageUnitCost(currentPrice: Double, amount: Int): Double {
        val totalCost = (this.amount * averageUnitCost) + (amount * currentPrice)
        val totalAmount = this.amount + amount
        return totalCost / totalAmount
    }
}