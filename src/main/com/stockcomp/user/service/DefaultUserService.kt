package com.stockcomp.user.service

import com.stockcomp.user.entity.Role
import com.stockcomp.user.dto.UserDetailsDto
import com.stockcomp.user.repository.UserRepository
import com.stockcomp.user.dto.mapToUserDetailsDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class DefaultUserService @Autowired constructor(
    private val userRepository: UserRepository
) : UserDetailsService, UserService {

    override fun loadUserByUsername(userName: String): UserDetails =
        userRepository.findByUsername(userName)
            .let { User(it.username, it.password, emptyList()) }


    override fun updateUserDetails(userDetailsDto: UserDetailsDto) {
        userRepository.findByUsername(userDetailsDto.username).let {
            it.country = userDetailsDto.country
            it.fullName = userDetailsDto.fullName
            userRepository.save(it)
        }
    }

    override fun getUserDetails(username: String): UserDetailsDto =
        mapToUserDetailsDto(userRepository.findByUsername(username))


    override fun verifyAdminUser(username: String): Boolean =
        userRepository.findByUsername(username).userRole == Role.ADMIN


    override fun findUserByUsername(username: String): com.stockcomp.user.entity.User? =
        userRepository.findByUsername(username)
}