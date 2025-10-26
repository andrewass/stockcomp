package com.stockcomp.user.internal

import com.stockcomp.common.BaseEntity
import jakarta.persistence.*

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
    val userRole: UserRole = UserRole.USER,
    @Enumerated(EnumType.STRING)
    val userStatus: UserStatus = UserStatus.ACTIVE,
) : BaseEntity()
