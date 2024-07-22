package com.stockcomp.participant.investmentorder

import com.stockcomp.common.entity.BaseEntity
import com.stockcomp.participant.investmentorder.OrderStatus
import com.stockcomp.participant.investmentorder.TransactionType
import com.stockcomp.participant.entity.Participant
import java.time.LocalDateTime
import jakarta.persistence.*

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