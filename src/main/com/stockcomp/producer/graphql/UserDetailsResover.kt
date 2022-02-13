package com.stockcomp.producer.graphql

import com.stockcomp.dto.user.UserDetailsDto
import com.stockcomp.service.user.UserService
import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.stereotype.Component


@Component
class UserDetailsResolver(
    private val userService: UserService
) : GraphQLQueryResolver {

    fun getUserDetails(username: String) : UserDetailsDto {
        return userService.getUserDetails(username)
    }
}