package com.stockcomp.producer.graphql

import com.stockcomp.dto.contest.InvestmentDto
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

    fun investment(symbol: String, contestNumber: Int, env: DataFetchingEnvironment): InvestmentDto? =
        participantService.getInvestmentForSymbol(extractUsername(env, jwtService), contestNumber, symbol)

    fun investments(contestNumber: Int, env: DataFetchingEnvironment): List<InvestmentDto> =
        participantService.getAllInvestmentsForContest(extractUsername(env, jwtService), contestNumber)
}