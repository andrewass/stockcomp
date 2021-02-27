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

    @OneToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    val user: User
)