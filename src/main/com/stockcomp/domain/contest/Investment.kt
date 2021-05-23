package com.stockcomp.domain.contest

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

    var amount : Int = 0
)