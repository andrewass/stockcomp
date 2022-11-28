package com.stockcomp.user.service

import com.stockcomp.user.dto.UserDetailsDto
import com.stockcomp.user.dto.mapToUserDetailsDto
import com.stockcomp.user.entity.Role
import com.stockcomp.user.entity.User
import com.stockcomp.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DefaultUserService @Autowired constructor(
    private val userRepository: UserRepository
) : UserService {

    override fun findUserByEmail(email: String): User = userRepository.findByEmail(email)


    override fun updateUserDetails(userDetailsDto: UserDetailsDto) {
        userRepository.findByEmail(userDetailsDto.email).let {
            it.country = userDetailsDto.country
            userRepository.save(it)
        }
    }

    override fun getUserDetails(username: String): UserDetailsDto =
        mapToUserDetailsDto(userRepository.findByUsername(username))


    override fun verifyAdminUser(username: String): Boolean =
        userRepository.findByUsername(username).userRole == Role.ADMIN


    override fun findUserByUsername(username: String): User? =
        userRepository.findByUsername(username)


    private fun createUser(email: String): User {
        return User(email = email, username = email)
            .let { userRepository.save(it) }
    }
}