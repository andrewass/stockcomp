package com.stockcomp.common

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.Version
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@MappedSuperclass
open class BaseEntity(
    @CreationTimestamp
    @Column(updatable = false)
    protected var dateCreated: LocalDateTime? = null,
    @UpdateTimestamp
    protected var dateUpdated: LocalDateTime? = null,
    @Version
    protected var version: Long? = null,
)
