package com.stockcomp.user.internal

import com.stockcomp.common.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "T_USER")
class User(
    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val userId: Long? = null,
    @Column(name = "USERNAME")
    private var _username: String,
    @Column(name = "FULL_NAME")
    private var _fullName: String? = null,
    val email: String,
    @Column(name = "COUNTRY")
    private var _country: String? = null,
    @Enumerated(EnumType.STRING)
    val userRole: UserRole = UserRole.USER,
    @Column(name = "USER_STATUS")
    @Enumerated(EnumType.STRING)
    private var _userStatus: UserStatus = UserStatus.ACTIVE,
) : BaseEntity() {
    constructor(
        email: String,
        username: String,
        fullName: String? = null,
        country: String? = null,
        userRole: UserRole = UserRole.USER,
        userStatus: UserStatus = UserStatus.ACTIVE,
        userId: Long? = null,
    ) : this(
        userId = userId,
        _username = username,
        _fullName = fullName,
        email = email,
        _country = country,
        userRole = userRole,
        _userStatus = userStatus,
    )

    val username: String
        get() = _username

    val fullName: String?
        get() = _fullName

    val country: String?
        get() = _country

    val userStatus: UserStatus
        get() = _userStatus

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val _userSubjects: MutableList<UserSubject> = mutableListOf()

    val userSubjects: List<UserSubject>
        get() = _userSubjects.toList()

    fun addUserSubject(userSubject: UserSubject) {
        _userSubjects.add(userSubject)
    }

    fun updateUserDetails(
        username: String,
        fullName: String?,
        country: String?,
    ) {
        this._username = username
        this._fullName = fullName
        this._country = country
    }

    fun updateStatus(userStatus: UserStatus) {
        this._userStatus = userStatus
    }
}
