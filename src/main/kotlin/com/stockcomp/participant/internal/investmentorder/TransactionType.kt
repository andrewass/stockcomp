package com.stockcomp.participant.internal.investmentorder

enum class TransactionType(
    val decode: String,
) {
    SELL("Sell"),
    BUY("Buy"),
}
