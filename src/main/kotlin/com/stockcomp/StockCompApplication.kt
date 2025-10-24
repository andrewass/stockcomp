package com.stockcomp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StockCompApplication

fun main(args: Array<String>) {
    runApplication<StockCompApplication>(*args)
}
