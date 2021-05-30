package com.stockcomp.domain.contest

enum class OrderStatus(val decode : String){
    ACTIVE("Active"),
    COMPLETED("Completed"),
    FAILED("Failed")
}