package com.stockcomp.user.service

import com.stockcomp.user.dto.UserDetailsDto
import com.stockcomp.user.entity.User
import org.springframework.data.domain.Page

interface UserService {

    fun getAllUsersSortedByEmail(pageNumber: Int, pageSize: Int) : Page<User>

    fun findUserByEmail(email: String): User?

    fun findUserByTokenClaim(email: String): User

    fun findUserByUsername(username: String): User

    fun updateUser(user: User, userDetailsDto: UserDetailsDto)

    fun getUserDetails(username: String): UserDetailsDto

    fun verifyAdminUser(username: String): Boolean
}