package com.stockcomp.response

import com.fasterxml.jackson.annotation.JsonProperty

class RealTimePriceResponse {
    @get:JsonProperty("currentPrice")
    @set:JsonProperty("c")
    var currentPrice: Double? = null

    @get:JsonProperty("highPrice")
    @set:JsonProperty("h")
    var highPrice: Double? = null

    @get:JsonProperty("lowPrice")
    @set:JsonProperty("l")
    var lowPrice: Double? = null

    @get:JsonProperty("openPrice")
    @set:JsonProperty("o")
    var openPrice: Double? = null

    @get:JsonProperty("previousClosePrice")
    @set:JsonProperty("pc")
    var previousClosePrice: Double? = null

    @get:JsonProperty("time")
    @set:JsonProperty("t")
    var time: Long? = null
}