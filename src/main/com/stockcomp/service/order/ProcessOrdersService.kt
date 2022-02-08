package com.stockcomp.service.order

interface ProcessOrdersService {

    suspend fun processInvestmentOrders()
}