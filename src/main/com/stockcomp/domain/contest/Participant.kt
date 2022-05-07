package com.stockcomp.domain.contest

import com.stockcomp.domain.BaseEntity
import com.stockcomp.domain.user.User
import com.stockcomp.investmentorder.entity.InvestmentOrder
import javax.persistence.*

@Entity
@Table(name = "T_PARTICIPANT")
class Participant(

    @Id
    @Column(name = "PARTICIPANT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    val user: User,

    @OneToMany(mappedBy = "participant", cascade = [CascadeType.REMOVE])
    val investmentOrders : MutableList<InvestmentOrder> = mutableListOf(),

    @OneToMany(mappedBy = "participant", cascade = [CascadeType.REMOVE])
    val investments: MutableList<Investment> = mutableListOf(),

    @ManyToOne
    @JoinColumn(name = "CONTEST_ID")
    val contest: Contest,

    var remainingFunds: Double = 20000.00,

    @Column(name = "PARTICIPANT_RANK")
    var rank: Int,

    var totalValue: Double = 20000.00,

    var totalInvestmentValue: Double = 0.00

) : BaseEntity()