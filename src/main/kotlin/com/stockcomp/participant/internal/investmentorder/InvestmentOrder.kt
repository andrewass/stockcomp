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
import java.math.BigDecimal
import java.math.RoundingMode
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
    @Column(name = "REMAINING_AMOUNT", nullable = false)
    private var _remainingAmount: Int = totalAmount,
    @Column(name = "ACCEPTED_PRICE", nullable = false, precision = 19, scale = 4)
    val acceptedPrice: BigDecimal,
    val currency: String,
    val expirationTime: LocalDateTime,
    @Enumerated(EnumType.STRING)
    val transactionType: TransactionType,
    @Enumerated(EnumType.STRING)
    @Column(name = "ORDER_STATUS", nullable = false)
    private var _orderStatus: OrderStatus = OrderStatus.ACTIVE,
    @Column(name = "ERROR_MESSAGE")
    private var _errorMessage: String? = null,
    @ManyToOne
    @JoinColumn(name = "PARTICIPANT_ID", nullable = false)
    val participant: Participant,
) : BaseEntity() {
    val remainingAmount: Int
        get() = _remainingAmount

    val orderStatus: OrderStatus
        get() = _orderStatus

    val errorMessage: String?
        get() = _errorMessage

    fun isActive(): Boolean = _orderStatus == OrderStatus.ACTIVE

    fun isCompleted(): Boolean = _orderStatus == OrderStatus.COMPLETED

    private fun isExpired(): Boolean = expirationTime.isBefore(LocalDateTime.now())

    fun processOrder(currentPrice: BigDecimal) {
        if (!isActive()) {
            return
        }

        if (isExpired()) {
            _orderStatus = OrderStatus.TERMINATED
            _errorMessage = "Order expired before execution"
            return
        }

        if (transactionType == TransactionType.BUY && participant.remainingFunds() >= currentPrice) {
            processBuyOrder(currentPrice)
        } else if (transactionType == TransactionType.SELL) {
            processSellOrder(currentPrice)
        }
    }

    private fun processBuyOrder(currentPrice: BigDecimal) {
        if (currentPrice <= acceptedPrice) {
            val amountToBuy = getAvailableAmountToBuy(currentPrice)
            participant.updateParticipantWhenBuying(amountToBuy, symbol, currentPrice)
            postProcessOrder(amountToBuy)
        }
    }

    private fun processSellOrder(currentPrice: BigDecimal) {
        if (currentPrice >= acceptedPrice) {
            val availableAmount = participant.getInvestmentAmount(symbol)
            if (availableAmount <= 0) {
                _orderStatus = OrderStatus.FAILED
                _errorMessage = "Cannot sell $symbol because participant does not own any units"
                return
            }
            val amountToSell = minOf(availableAmount, _remainingAmount)
            participant.updateParticipantWhenSelling(amountToSell, symbol, currentPrice)
            postProcessOrder(amountToSell)
        }
    }

    private fun getAvailableAmountToBuy(currentPrice: BigDecimal): Int {
        val maxAvailableAmount =
            participant
                .remainingFunds()
                .divide(currentPrice, 0, RoundingMode.DOWN)
                .toInt()
        return minOf(maxAvailableAmount, _remainingAmount)
    }

    private fun postProcessOrder(amount: Int) {
        _remainingAmount -= amount
        if (_remainingAmount == 0) {
            _orderStatus = OrderStatus.COMPLETED
        }
    }
}
