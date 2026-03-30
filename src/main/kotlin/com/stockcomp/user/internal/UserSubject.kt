package com.stockcomp.user.internal

import com.stockcomp.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "T_USER_SUBJECT")
class UserSubject(
    @Id
    @Column(name = "USER_SUBJECT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val userSubjectId: Long? = null,
    @Enumerated(EnumType.STRING)
    val subjectProvider: SubjectProvider,
    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    val user: User,
    @Column(name = "IS_VALID", nullable = false)
    val isValid: Boolean,
    @Column(name = "EXTERNAL_SUBJECT_ID", nullable = false)
    val externalSubjectId: String,
) : BaseEntity()
