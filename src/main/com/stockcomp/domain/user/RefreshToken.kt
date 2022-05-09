package com.stockcomp.domain.user

import com.stockcomp.domain.BaseEntity
import com.stockcomp.user.entity.User
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

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    val user: User,

    val expirationTime: LocalDateTime

) : BaseEntity()