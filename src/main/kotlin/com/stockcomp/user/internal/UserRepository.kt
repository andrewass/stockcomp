package com.stockcomp.user.internal

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u._username = :username")
    fun findByUsername(username: String): User

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u._username = :username")
    fun existsByUsername(username: String): Boolean

    fun findByEmail(email: String): User?

    @Query(
        """
        SELECT US.user
        FROM UserSubject US
        WHERE US.externalSubjectId = :userSubject
        AND US.isValid = true
        """,
    )
    fun findByUserSubject(userSubject: String): User?
}
