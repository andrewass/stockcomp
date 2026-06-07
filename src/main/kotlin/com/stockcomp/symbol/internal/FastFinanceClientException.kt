package com.stockcomp.symbol.internal

class FastFinanceClientException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
