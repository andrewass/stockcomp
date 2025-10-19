package com.stockcomp.participant.internal.investmentorder

enum class OrderStatus(val decode : String){
    ACTIVE("Active"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    TERMINATED("Terminated")
}