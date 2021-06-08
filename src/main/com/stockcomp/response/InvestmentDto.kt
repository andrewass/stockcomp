package com.stockcomp.response

data class InvestmentDto(
    val symbol : String,
    val amount : Int,
    val sumPaid : Double,
    val investmentReturns : Double
)