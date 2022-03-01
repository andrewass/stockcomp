package com.stockcomp.producer.graphql

import com.stockcomp.dto.leaderboard.LeaderboardEntryDto
import com.stockcomp.dto.user.UserDetailsDto
import com.stockcomp.service.leaderboard.LeaderboardService
import graphql.kickstart.tools.GraphQLQueryResolver
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component


@Component
class LeaderboardQueryResolvers(
    private val leaderboardService: LeaderboardService
) : GraphQLQueryResolver{

    fun leaderboardEntry(userDetails: UserDetailsDto): LeaderboardEntryDto? =
        leaderboardService.getLeaderboardEntryForUser(userDetails.username)

    fun sortedLeaderboardEntries(env : DataFetchingEnvironment) : List<LeaderboardEntryDto> =
        leaderboardService.getSortedLeaderboardEntries()
}