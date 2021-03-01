package com.stockcomp.entity

import java.time.LocalDate
import javax.persistence.*


@Entity
@Table(name = "T_USER")
class User(

    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val username: String,

    val password: String,

    val email: String
)
