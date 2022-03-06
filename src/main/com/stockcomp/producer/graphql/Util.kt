package com.stockcomp.producer.graphql

import com.stockcomp.producer.common.getAccessTokenFromCookie
import com.stockcomp.service.security.JwtService
import graphql.kickstart.servlet.context.DefaultGraphQLServletContext
import graphql.schema.DataFetchingEnvironment


fun extractUsername(env: DataFetchingEnvironment, jwtService: JwtService): String =
    env.getContext<DefaultGraphQLServletContext>().httpServletRequest
        .let { getAccessTokenFromCookie(it) }
        .let { jwtService.extractUsername(it!!) }

