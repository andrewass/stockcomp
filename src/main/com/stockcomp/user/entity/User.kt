package com.stockcomp.user.entity

import com.stockcomp.common.entity.BaseEntity
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

    var country: String? = null,

    var fullName: String? = null,

    @Enumerated(EnumType.STRING)
    val userRole: Role = Role.USER

) : BaseEntity()