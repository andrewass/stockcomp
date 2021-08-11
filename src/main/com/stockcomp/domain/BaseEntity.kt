package com.stockcomp.domain

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