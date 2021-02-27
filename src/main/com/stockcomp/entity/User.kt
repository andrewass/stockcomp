package com.stockcomp.entity

import java.time.LocalDate
import javax.persistence.*


@Entity
@Table(name = "T_USER")
class User(

    @Id
    @Column(name = "T_CONTEST")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val startTime: LocalDate?
)
