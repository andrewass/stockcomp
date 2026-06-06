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
import java.math.BigDecimal
import java.math.RoundingMode

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
    @Column(name = "AVERAGE_UNIT_COST", nullable = false, precision = 19, scale = 4)
    private var _averageUnitCost: BigDecimal = BigDecimal.ZERO,
    @Column(name = "TOTAL_PROFIT", nullable = false, precision = 19, scale = 4)
    private var _totalProfit: BigDecimal = BigDecimal.ZERO,
    @Column(name = "TOTAL_VALUE", nullable = false, precision = 19, scale = 4)
    private var _totalValue: BigDecimal = BigDecimal.ZERO,
) : BaseEntity() {
    val amount: Int
        get() = _amount

    val averageUnitCost: BigDecimal
        get() = _averageUnitCost

    val totalProfit: BigDecimal
        get() = _totalProfit

    val totalValue: BigDecimal
        get() = _totalValue

    fun updateWhenBuying(
        amount: Int,
        currentPrice: BigDecimal,
    ) {
        require(amount > 0) { "Buy amount must be positive for symbol $symbol" }
        require(currentPrice > BigDecimal.ZERO) { "Current price must be positive for symbol $symbol" }

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

    fun maintainInvestment(updatedPrice: BigDecimal) {
        val newTotalValueInvestment = updatedPrice.multiply(BigDecimal.valueOf(_amount.toLong()))
        _totalValue = newTotalValueInvestment
        _totalProfit = newTotalValueInvestment.subtract(_averageUnitCost.multiply(BigDecimal.valueOf(_amount.toLong())))
    }

    private fun calculateAverageUnitCost(
        currentPrice: BigDecimal,
        amount: Int,
    ): BigDecimal {
        val totalCost =
            _averageUnitCost
                .multiply(BigDecimal.valueOf(_amount.toLong()))
                .add(currentPrice.multiply(BigDecimal.valueOf(amount.toLong())))
        val totalAmount = _amount + amount
        return totalCost.divide(BigDecimal.valueOf(totalAmount.toLong()), 10, RoundingMode.HALF_UP)
    }
}
