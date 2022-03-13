package com.stockcomp.producer.graphql

import com.stockcomp.domain.contest.Investment
import com.stockcomp.service.participant.ParticipantService
import com.stockcomp.service.security.JwtService
import graphql.kickstart.tools.GraphQLQueryResolver
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class InvestmentQueryResolvers(
    private val participantService: ParticipantService,
    private val jwtService: JwtService
) : GraphQLQueryResolver {

    fun investment(symbol: String, contestNumber: Int, env: DataFetchingEnvironment): Investment? =
        participantService.getInvestmentForSymbol(extractUsername(env, jwtService), contestNumber, symbol)

    fun investments(contestNumber: Int, env: DataFetchingEnvironment): List<Investment> =
        participantService.getAllInvestmentsForContest(extractUsername(env, jwtService), contestNumber)
}