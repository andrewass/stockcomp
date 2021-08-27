package com.stockcomp.response

data class InvestmentDto(
    val symbol : String,
    val amount : Int,
    val averagePricePaid : Double,
    val investmentReturns : Double
)