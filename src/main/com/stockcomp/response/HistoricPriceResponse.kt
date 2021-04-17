package com.stockcomp.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class HistoricPriceResponse(
    @JsonProperty("price")
    val price : Double,

    @JsonProperty("date")
    val date : LocalDate
)
