package com.stockcomp.producer.graphql

import com.stockcomp.contest.entity.Contest
import com.stockcomp.participant.entity.Participant
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.participant.dto.ContestParticipantDto
import com.stockcomp.contest.service.ContestService
import com.stockcomp.service.security.JwtService
import graphql.kickstart.tools.GraphQLMutationResolver
import graphql.kickstart.tools.GraphQLQueryResolver
import graphql.kickstart.tools.GraphQLResolver
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class ContestQueryResolvers(
    private val contestService: ContestService,
    private val jwtService: JwtService
) : GraphQLQueryResolver {

    fun contest(contestNumber: Int): Contest = contestService.getContest(contestNumber)

    fun contests(statusList: List<ContestStatus>) = contestService.getContests(statusList)

    fun contestParticipants(
        statusList: List<ContestStatus>, env: DataFetchingEnvironment
    ): List<ContestParticipantDto> =
        contestService.getContestParticipants(statusList, extractUsername(env, jwtService))

    fun sortedParticipants(contestNumber: Int) : List<Participant> =
        contestService.getSortedParticipantsByRank(contestNumber)
}


@Component
class ContestMutationResolvers(
    private val contestService: ContestService,
    private val jwtService: JwtService
) : GraphQLMutationResolver {

    fun signUpContest(contestNumber: Int, env: DataFetchingEnvironment) {
        contestService.signUpUser(extractUsername(env, jwtService), contestNumber)
    }
}

@Component
class ContestResolver : GraphQLResolver<Contest> {

    fun contestStatus(contest: Contest) = contest.contestStatus
}

@Component
class ParticipantResolver : GraphQLResolver<Participant> {

    fun username(participant: Participant) = participant.user.username

    fun country(participant: Participant) = participant.user.country
}



