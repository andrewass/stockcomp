package com.stockcomp.entity.contest

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "T_CONTEST")
class Contest(

    @Id
    @Column(name = "CONTEST_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val startTime: LocalDateTime,

    val contestNumber: Int,

    var inPreStartMode: Boolean = true,

    var inRunningMode: Boolean = false,

    @OneToMany(mappedBy = "contest", cascade = [CascadeType.ALL])
    val participants: MutableList<Participant> = mutableListOf()
)