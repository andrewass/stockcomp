package com.stockcomp.producer.graphql

import com.stockcomp.leaderboard.dto.LeaderboardEntryDto
import com.stockcomp.leaderboard.service.LeaderboardService
import com.stockcomp.service.security.JwtService
import graphql.kickstart.tools.GraphQLQueryResolver
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component


@Component
class LeaderboardQueryResolvers(
    private val leaderboardService: LeaderboardService,
    private val jwtService: JwtService
) : GraphQLQueryResolver{

    fun leaderboardEntry(env : DataFetchingEnvironment): LeaderboardEntryDto? =
        leaderboardService.getLeaderboardEntryForUser(extractUsername(env, jwtService))

    fun sortedLeaderboardEntries() : List<LeaderboardEntryDto> =
        leaderboardService.getSortedLeaderboardEntries()
}