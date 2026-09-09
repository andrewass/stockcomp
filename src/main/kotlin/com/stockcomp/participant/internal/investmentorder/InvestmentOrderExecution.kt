package com.stockcomp.participant.internal.investmentorder

import com.stockcomp.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "T_INVESTMENT_ORDER_EXECUTION")
class InvestmentOrderExecution(
    @Id
    @Column(name = "INVESTMENT_ORDER_EXECUTION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val executionId: Long? = null,
    @Column(name = "AMOUNT", nullable = false)
    val amount: Int,
    @Column(name = "EXECUTION_PRICE", nullable = false, precision = 19, scale = 4)
    val executionPrice: BigDecimal,
    @Column(name = "EXECUTED_AT", nullable = false)
    val executedAt: Instant,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INVESTMENT_ORDER_ID", nullable = false)
    val investmentOrder: InvestmentOrder,
) : BaseEntity() {
    init {
        require(amount > 0) { "Execution amount must be positive" }
        require(executionPrice > BigDecimal.ZERO) { "Execution price must be positive" }
    }
}
