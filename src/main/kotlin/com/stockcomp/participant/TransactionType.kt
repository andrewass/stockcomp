package com.stockcomp.participant

enum class TransactionType(
    val decode: String,
) {
    SELL("Sell"),
    BUY("Buy"),
}
