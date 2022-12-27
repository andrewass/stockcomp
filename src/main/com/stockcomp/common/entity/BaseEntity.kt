package com.stockcomp.common.entity

import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@MappedSuperclass
open class BaseEntity(

        @CreationTimestamp
        val dateCreated: LocalDateTime? = null,

        @UpdateTimestamp
        val dateUpdated: LocalDateTime? = null
)