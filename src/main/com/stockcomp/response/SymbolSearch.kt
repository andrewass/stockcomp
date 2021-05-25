package com.stockcomp.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class SymbolSearch(
    @JsonProperty("description")
    val description: String,

    @JsonProperty("symbol")
    val symbol: String
)