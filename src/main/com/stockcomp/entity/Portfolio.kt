package com.stockcomp.entity

import javax.persistence.*


@Entity
@Table(name = "T_PORTFOLIO")
class Portfolio (

    @Id
    @Column(name = "PORTFOLIO_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null
)