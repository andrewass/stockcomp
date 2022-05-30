package com.stockcomp.common.entity

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class BaseEntity(

        @CreationTimestamp
        val dateCreated: LocalDateTime? = null,

        @UpdateTimestamp
        val dateUpdated: LocalDateTime? = null
)