package com.stockcomp.entity.contest

enum class OrderStatus(decode: String) {
    ACTIVE("Active"),
    COMPLETED("Completed"),
    FAILED("Failed")
}