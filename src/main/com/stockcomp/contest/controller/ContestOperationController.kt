package com.stockcomp.contest.controller

import com.stockcomp.exception.handler.CustomExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/contest-operations")
class ContestOperationController(

): CustomExceptionHandler() {

    @PostMapping("/maintain-contest-status")
    fun maintainContestStatus(){
        
    }

    @PostMapping("/maintain-investments")
    fun maintainInvestments(){

    }

    @PostMapping("/process-investment-orders")
    fun processInvestmentOrders(){

    }
}