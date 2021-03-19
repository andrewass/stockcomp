package com.stockcomp.entity.contest

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "T_TRANSACTION")
class Transaction(

    @Id
    @Column(name = "TRANSACTION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    val transactionType : TransactionType,

    @ManyToOne
    @JoinColumn(name = "PARTICIPANT_ID", nullable = false)
    val participant: Participant,

    val symbol: String,

    val amount: Int,

    val currentPrice: Double,

    val dateTimeProcessed : LocalDateTime
)