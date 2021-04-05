package com.stockcomp.response

import com.fasterxml.jackson.annotation.JsonProperty

data class RealTimePriceResponse(
    @get:JsonProperty("currentPrice")
    @set:JsonProperty("c")
    var currentPrice: Double,

    @get:JsonProperty("highPrice")
    @set:JsonProperty("h")
    var highPrice: Double,

    @get:JsonProperty("lowPrice")
    @set:JsonProperty("l")
    var lowPrice: Double,

    @get:JsonProperty("openPrice")
    @set:JsonProperty("o")
    var openPrice: Double,

    @get:JsonProperty("previousClosePrice")
    @set:JsonProperty("pc")
    var previousClosePrice: Double,

    @get:JsonProperty("time")
    @set:JsonProperty("t")
    var time: Long
)