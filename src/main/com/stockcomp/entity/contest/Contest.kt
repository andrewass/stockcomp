package com.stockcomp.entity.contest

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "T_CONTEST")
class Contest(

    @Id
    @Column(name = "T_CONTEST")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val startTime: LocalDate?
)