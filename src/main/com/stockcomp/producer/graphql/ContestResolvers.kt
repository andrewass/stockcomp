package com.stockcomp.producer.graphql

import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.dto.contest.ContestDto
import com.stockcomp.dto.contest.ContestParticipantDto
import com.stockcomp.service.contest.ContestService
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

    fun contest(contestNumber: Int): ContestDto = contestService.getContest(contestNumber)

    fun contestParticipants(
        statusList: List<ContestStatus>, env: DataFetchingEnvironment
    ): List<ContestParticipantDto> =
        contestService.getContestParticipants(statusList, extractUsername(env, jwtService))
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
class ContestResolver : GraphQLResolver<ContestDto> {

    fun contestStatus(contestDto: ContestDto) = contestDto.contestStatus
}
