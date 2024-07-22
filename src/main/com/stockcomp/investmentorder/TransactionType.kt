package com.stockcomp.investmentorder

enum class TransactionType(val decode : String) {
    SELL("Sell"),
    BUY("Buy")
}