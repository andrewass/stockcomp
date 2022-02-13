package com.stockcomp.producer.graphql

import com.stockcomp.dto.leaderboard.LeaderboardEntryDto
import com.stockcomp.dto.user.UserDetailsDto
import com.stockcomp.service.leaderboard.LeaderboardService
import com.stockcomp.service.user.UserService
import graphql.kickstart.tools.GraphQLQueryResolver
import graphql.kickstart.tools.GraphQLResolver
import org.springframework.stereotype.Component


@Component
class UserDetailsResolver(
    private val userService: UserService
) : GraphQLQueryResolver {

    fun getUserDetails(username: String): UserDetailsDto = userService.getUserDetails(username)
}

@Component
class UserDetailsLeaderboardEntryResolver(
    private val leaderboardService: LeaderboardService
) : GraphQLResolver<UserDetailsDto> {

    fun leaderboardEntry(userDetails: UserDetailsDto): LeaderboardEntryDto? =
        leaderboardService.getLeaderboardEntryForUser(userDetails.username)
}