package com.stockcomp.service

import com.stockcomp.consumer.StockConsumer
import com.stockcomp.response.RealTimePriceResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StockService @Autowired constructor(
    private val stockConsumer: StockConsumer
){

    fun getRealTimePrice(symbol: String): RealTimePriceResponse {
        return stockConsumer.findRealTimePrice(symbol)
    }
}