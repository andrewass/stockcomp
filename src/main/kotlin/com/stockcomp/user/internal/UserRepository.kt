package com.stockcomp.user.internal

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User

    fun existsByUsername(username: String): Boolean

    @Query("SELECT U FROM User U JOIN UserSubject US ON US.externalSubjectId = :userSubject")
    fun findByUserSubject(userSubject: String): User?
}
