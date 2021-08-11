package com.stockcomp.domain.contest

import com.stockcomp.domain.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "T_INVESTMENT")
class Investment(

    @Id
    @Column(name = "INVESTMENT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "INVESTMENT_NAME")
    val name: String,

    val symbol: String,

    @ManyToOne
    @JoinColumn(name = "PORTFOLIO_ID", nullable = false)
    val portfolio: Portfolio,

    var amount: Int = 0,

    var totalAmountBought : Int = 0,

    var sumPaid: Double = 0.00,

    var investmentReturns : Double = 0.00,

    var totalValue : Double = 0.00

) : BaseEntity()