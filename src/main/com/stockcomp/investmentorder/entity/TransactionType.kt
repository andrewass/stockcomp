package com.stockcomp.investmentorder.entity

enum class TransactionType(val decode : String) {
    SELL("Sell"),
    BUY("Buy")
}