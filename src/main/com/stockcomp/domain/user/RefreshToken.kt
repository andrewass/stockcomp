package com.stockcomp.domain.user

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "T_REFRESH_TOKEN")
class RefreshToken(

    @Id
    @Column(name = "REFRESH_TOKEN_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val token: String,

    @OneToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    val user: User,

    val expirationTime: LocalDateTime
)