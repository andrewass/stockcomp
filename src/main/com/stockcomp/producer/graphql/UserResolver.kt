package com.stockcomp.producer.graphql

import com.stockcomp.dto.user.UserDetailsDto
import com.stockcomp.service.user.UserService
import graphql.kickstart.tools.GraphQLMutationResolver
import graphql.kickstart.tools.GraphQLQueryResolver
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component


@Component
class UserQueryResolver(
    private val userService: UserService
) : GraphQLQueryResolver {

    fun userDetails(username: String?, env: DataFetchingEnvironment): UserDetailsDto {
        return if (username != null) {
            userService.getUserDetails(username)
        } else {
            userService.getUserDetails(extractUsername(env))
        }
    }
}

@Component
class UserMutationResolver(
    private val userService: UserService
) : GraphQLMutationResolver {

}