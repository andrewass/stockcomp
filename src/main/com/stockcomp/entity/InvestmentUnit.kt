package com.stockcomp.entity

import com.stockcomp.util.extractUsername
import javax.persistence.*

@Entity
@Table(name = "T_INVESTMENT_UNIT")
class InvestmentUnit(

    @Id
    @Column(name = "INVESTMENT_UNIT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val symbol: String,

    @Column(name = "INVESTMENT_NAME")
    val name: String? = null,

    var transactions: Int = 0
)