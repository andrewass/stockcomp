package com.stockcomp.domain.contest

import com.stockcomp.domain.user.User
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

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "PORTFOLIO_ID")
    val portfolio : Portfolio = Portfolio(),

    @OneToMany(mappedBy = "participant", cascade = [CascadeType.ALL])
    val investmentOrders : MutableList<InvestmentOrder> = mutableListOf(),

    @ManyToOne
    @JoinColumn(name = "CONTEST_ID")
    val contest: Contest,

    var remainingFund: Double = 20000.00,

    @Column(name = "participant_rank")
    var rank: Int? = null,

    @Column(name = "participant_score")
    var score: Int? = null
)