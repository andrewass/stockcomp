package com.stockcomp.domain.contest

import javax.persistence.*

@Entity
@Table(name = "T_PORTFOLIO")
class Portfolio (

    @Id
    @Column(name = "PORTFOLIO_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToMany(mappedBy = "portfolio", cascade = [CascadeType.ALL])
    val investments : MutableList<Investment> = mutableListOf()
)