package com.stockcomp.producer.graphql

import com.stockcomp.dto.contest.ContestDto
import com.stockcomp.service.contest.ContestService
import graphql.kickstart.tools.GraphQLMutationResolver
import graphql.kickstart.tools.GraphQLQueryResolver
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class ContestQueryResolvers(
    private val contestService: ContestService
) : GraphQLQueryResolver {

    fun contest(contestNumber: Int): ContestDto = contestService.getContest(contestNumber)
}


@Component
class ContestMutationResolvers(
    private val contestService: ContestService
) : GraphQLMutationResolver {

    fun signUpContest(contestNumber: Int, env: DataFetchingEnvironment) {
        contestService.signUpUser("", contestNumber)
    }
}