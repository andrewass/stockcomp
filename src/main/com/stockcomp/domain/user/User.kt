package com.stockcomp.domain.user

import com.stockcomp.domain.BaseEntity
import javax.persistence.*

@Entity
@Table(name = "T_USER")
class User(

    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val username: String,

    var password: String,

    val email: String,

    var country: String,

    @Enumerated(EnumType.STRING)
    val userRole: Role = Role.USER,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.REMOVE])
    val refreshTokens: MutableList<RefreshToken> = mutableListOf()

) : BaseEntity()