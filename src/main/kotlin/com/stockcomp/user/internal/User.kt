package com.stockcomp.user.internal

import com.stockcomp.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

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
