package com.stockcomp.user.repository

import com.stockcomp.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByUsername(username: String): User

    fun findByEmail(email: String): User

    fun existsByUsername(username: String): Boolean
}