package com.stockcomp.entity.contest

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "T_INVESTMENT_ORDER")
class InvestmentOrder(

    @Id
    @Column(name = "INVESTMENT_ORDER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val symbol: String,

    val totalAmount: Int,

    var remainingAmount: Int = totalAmount,

    val acceptedPrice: Double,

    val expirationTime: LocalDateTime,

    val activeOrder: Boolean = true,

    @Enumerated(EnumType.STRING)
    val transactionType: TransactionType,

    @Enumerated(EnumType.STRING)
    var orderStatus: OrderStatus = OrderStatus.ACTIVE,

    @ManyToOne
    @JoinColumn(name = "PARTICIPANT_ID", nullable = false)
    val participant: Participant
)