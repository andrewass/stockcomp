package com.stockcomp.domain.contest.enums

enum class OrderStatus(val decode : String){
    ACTIVE("Active"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    TERMINATED("Terminated")
}