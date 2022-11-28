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

    var username: String,

    var fullName: String? = null,

    val email: String,

    var country: String? = null,

    @Enumerated(EnumType.STRING)
    val userRole: Role = Role.USER

) : BaseEntity()