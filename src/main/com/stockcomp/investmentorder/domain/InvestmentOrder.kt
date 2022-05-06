package com.stockcomp.investmentorder.domain

import com.stockcomp.domain.BaseEntity
import com.stockcomp.domain.contest.Participant
import com.stockcomp.domain.contest.enums.OrderStatus
import com.stockcomp.domain.contest.enums.TransactionType
import java.time.LocalDateTime
import javax.persistence.*

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

    val currency : String,

    val expirationTime: LocalDateTime,

    @Enumerated(EnumType.STRING)
    val transactionType: TransactionType,

    @Enumerated(EnumType.STRING)
    var orderStatus: OrderStatus = OrderStatus.ACTIVE,

    var errorMessage : String? = null,

    @ManyToOne
    @JoinColumn(name = "PARTICIPANT_ID", nullable = false)
    val participant: Participant

) : BaseEntity()