package com.stockcomp.investment.service

import org.springframework.stereotype.Service

interface InvestmentTaskService {
    fun maintainInvestments(participantId: Long)
}

@Service
class DefaultInvestmentTaskService : InvestmentTaskService {

    override fun maintainInvestments(participantId: Long) {
    }
}