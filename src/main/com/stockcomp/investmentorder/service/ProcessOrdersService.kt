package com.stockcomp.investmentorder.service

interface ProcessOrdersService {

    suspend fun processInvestmentOrders()
}