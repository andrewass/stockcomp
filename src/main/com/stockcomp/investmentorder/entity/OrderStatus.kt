package com.stockcomp.investmentorder.entity

enum class OrderStatus(val decode : String){
    ACTIVE("Active"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    TERMINATED("Terminated")
}