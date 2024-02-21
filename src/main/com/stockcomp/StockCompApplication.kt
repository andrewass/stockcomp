package com.stockcomp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class StockCompApplication

fun main(args: Array<String>) {
    runApplication<StockCompApplication>(*args)
}
