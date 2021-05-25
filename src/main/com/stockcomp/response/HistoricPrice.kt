package com.stockcomp.response

import java.time.LocalDate

data class HistoricPrice(
    val price: Double,
    val date: LocalDate
)
