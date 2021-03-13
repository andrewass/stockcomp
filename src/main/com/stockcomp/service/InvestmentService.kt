package com.stockcomp.service

import com.stockcomp.consumer.StockConsumer
import com.stockcomp.entity.contest.Participant
import com.stockcomp.repository.ContestRepository
import com.stockcomp.repository.ParticipantRepository
import com.stockcomp.request.InvestmentTransactionRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class InvestmentService @Autowired constructor(
    private val contestRepository: ContestRepository,
    private val participantRepository: ParticipantRepository,
    private val stockConsumer : StockConsumer
) {

    fun buyInvestment(request: InvestmentTransactionRequest) {
        val participant = getParticipant(request.username, request.contestNumber)
        val realTimePrice = stockConsumer.findRealTimePrice(request.investment)
    }

    fun sellInvestment(request: InvestmentTransactionRequest) {
        val participant = getParticipant(request.username, request.contestNumber)
        val realTimePrice = stockConsumer.findRealTimePrice(request.investment)

    }

    private fun getParticipant(username : String, contestNumber : Int) : Participant {
        val contest = contestRepository.findContestByContestNumberAndInRunningModeIsTrue(contestNumber)

        return participantRepository.findParticipantFromUsername(username, contest.get()).stream().findFirst().get()
    }
}