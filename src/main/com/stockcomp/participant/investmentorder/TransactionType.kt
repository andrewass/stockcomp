package com.stockcomp.participant.investmentorder

enum class TransactionType(val decode : String) {
    SELL("Sell"),
    BUY("Buy")
}