package com.stockcomp.entity.contest

import com.stockcomp.entity.User
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

    @ManyToOne
    @JoinColumn(name = "CONTEST_ID")
    val contest: Contest

    /*
    var fund: Int = 200000

    var rank: Int? = null,

    var score: Int = 0
     */
)