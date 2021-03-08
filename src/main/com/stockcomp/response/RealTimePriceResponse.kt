package com.stockcomp.response

import com.fasterxml.jackson.annotation.JsonProperty

class RealTimePriceResponse {
    @get:JsonProperty("currentPrice")
    @set:JsonProperty("c")
    var currentPrice: Int? = null

    @get:JsonProperty("highPrice")
    @set:JsonProperty("h")
    var highPrice: Int? = null

    @get:JsonProperty("lowPrice")
    @set:JsonProperty("l")
    var lowPrice: Int? = null

    @get:JsonProperty("openPrice")
    @set:JsonProperty("o")
    var openPrice: Int? = null

    @get:JsonProperty("previousClosePrice")
    @set:JsonProperty("pc")
    var previousClosePrice: Int? = null

    @get:JsonProperty("time")
    @set:JsonProperty("t")
    var time: Int? = null
}