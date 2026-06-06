package com.stockcomp.symbol

import java.math.BigDecimal

data class CurrentPriceSymbolDto(
    val symbol: String,
    val companyName: String,
    val currentPrice: BigDecimal,
    val previousClose: BigDecimal,
    val currency: String,
    val percentageChange: BigDecimal,
    val usdPrice: BigDecimal,
)
