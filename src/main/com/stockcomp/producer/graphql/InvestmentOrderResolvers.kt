package com.stockcomp.producer.graphql

import com.stockcomp.domain.contest.InvestmentOrder
import com.stockcomp.domain.contest.enums.OrderStatus
import com.stockcomp.service.order.InvestmentOrderService
import com.stockcomp.service.security.JwtService
import graphql.kickstart.tools.GraphQLQueryResolver
import graphql.kickstart.tools.GraphQLResolver
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class InvestmentOrderQueryResolvers(
    private val investmentOrderService: InvestmentOrderService,
    private val jwtService: JwtService
) : GraphQLQueryResolver {

    fun investmentOrders(contestNumber: Int, statusList: List<OrderStatus>, env: DataFetchingEnvironment):
            List<InvestmentOrder> =
        investmentOrderService.getOrdersByStatus(extractUsername(env, jwtService), contestNumber, statusList)


    fun investmentOrdersSymbol(
        symbol: String, contestNumber: Int,
        statusList: List<OrderStatus>, env: DataFetchingEnvironment
    ): List<InvestmentOrder> =
        investmentOrderService.getSymbolOrdersByStatus(
            extractUsername(env, jwtService),
            contestNumber,
            statusList,
            symbol
        )
}

@Component
class InvestmentOrderResolver : GraphQLResolver<InvestmentOrder> {

    fun orderStatus(investmentOrder: InvestmentOrder) = investmentOrder.orderStatus

    fun transactionType(investmentOrder: InvestmentOrder) = investmentOrder.transactionType
}
