package com.stockcomp.participant.internal.investmentorder

import com.stockcomp.common.BaseEntity
import com.stockcomp.participant.internal.Participant
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "T_INVESTMENT_ORDER")
class InvestmentOrder(
    @Id
    @Column(name = "INVESTMENT_ORDER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val orderId: Long? = null,
    val symbol: String,
    val totalAmount: Int,
    var remainingAmount: Int = totalAmount,
    val acceptedPrice: Double,
    val currency: String,
    val expirationTime: LocalDateTime,
    @Enumerated(EnumType.STRING)
    val transactionType: TransactionType,
    @Enumerated(EnumType.STRING)
    var orderStatus: OrderStatus = OrderStatus.ACTIVE,
    var errorMessage: String? = null,
    @ManyToOne
    @JoinColumn(name = "PARTICIPANT_ID", nullable = false)
    val participant: Participant,
) : BaseEntity() {
    fun isActive(): Boolean = orderStatus == OrderStatus.ACTIVE

    fun isCompleted(): Boolean = orderStatus == OrderStatus.COMPLETED

    fun processOrder(currentPrice: Double) {
        if (transactionType == TransactionType.BUY && participant.remainingFunds >= currentPrice) {
            processBuyOrder(currentPrice)
        } else if (transactionType == TransactionType.SELL) {
            processSellOrder(currentPrice)
        }
    }

    private fun processBuyOrder(currentPrice: Double) {
        if (currentPrice <= acceptedPrice) {
            val amountToBuy = getAvailableAmountToBuy(currentPrice)
            participant.updateParticipantWhenBuying(amountToBuy, symbol, currentPrice)
            postProcessOrder(amountToBuy)
        }
    }

    private fun processSellOrder(currentPrice: Double) {
        if (currentPrice >= acceptedPrice) {
            val availableAmount = participant.getInvestmentAmount(symbol)
            val amountToSell = Integer.min(availableAmount, remainingAmount)
            participant.updateParticipantWhenSelling(amountToSell, symbol, currentPrice)
            postProcessOrder(amountToSell)
        }
    }

    private fun getAvailableAmountToBuy(currentPrice: Double): Int {
        val maxAvailAmount = participant.remainingFunds / currentPrice
        return Integer.min(maxAvailAmount.toInt(), remainingAmount)
    }

    private fun postProcessOrder(amount: Int) {
        remainingAmount -= amount
        if (remainingAmount == 0) {
            orderStatus = OrderStatus.COMPLETED
        }
    }
}
