package com.stockcomp.response

import java.time.LocalDate

data class HistoricPriceResponse(
    val price: Double,
    val date: LocalDate
)
