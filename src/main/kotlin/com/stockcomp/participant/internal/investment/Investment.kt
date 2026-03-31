package com.stockcomp.participant.internal.investment

import com.stockcomp.common.BaseEntity
import com.stockcomp.participant.internal.Participant
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

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
    @Column(name = "AMOUNT", nullable = false)
    private var _amount: Int = 0,
    @Column(name = "AVERAGE_UNIT_COST", nullable = false)
    private var _averageUnitCost: Double = 0.00,
    @Column(name = "TOTAL_PROFIT", nullable = false)
    private var _totalProfit: Double = 0.00,
    @Column(name = "TOTAL_VALUE", nullable = false)
    private var _totalValue: Double = 0.00,
) : BaseEntity() {
    val amount: Int
        get() = _amount

    val averageUnitCost: Double
        get() = _averageUnitCost

    val totalProfit: Double
        get() = _totalProfit

    val totalValue: Double
        get() = _totalValue

    fun updateWhenBuying(
        amount: Int,
        currentPrice: Double,
    ) {
        require(amount > 0) { "Buy amount must be positive for symbol $symbol" }
        require(currentPrice > 0.0) { "Current price must be positive for symbol $symbol" }

        _averageUnitCost = calculateAverageUnitCost(currentPrice = currentPrice, amount = amount)
        _amount += amount
    }

    fun updateWhenSelling(amount: Int) {
        require(amount > 0) { "Sell amount must be positive for symbol $symbol" }
        require(_amount >= amount) {
            "Cannot sell $amount units for $symbol when only $_amount are available"
        }
        _amount -= amount
    }

    fun maintainInvestment(updatedPrice: Double) {
        val newTotalValueInvestment = _amount * updatedPrice
        _totalValue = newTotalValueInvestment
        _totalProfit = newTotalValueInvestment - (_amount * _averageUnitCost)
    }

    private fun calculateAverageUnitCost(
        currentPrice: Double,
        amount: Int,
    ): Double {
        val totalCost = (_amount * _averageUnitCost) + (amount * currentPrice)
        val totalAmount = _amount + amount
        return totalCost / totalAmount
    }
}
