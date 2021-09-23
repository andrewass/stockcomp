package com.stockcomp.service.contest

import com.stockcomp.response.ParticipantDto
import com.stockcomp.response.UpcomingContest

interface ContestService {

    fun startContest(contestNumber: Int)

    fun stopContest(contestNumber: Int)

    fun completeContest(contestNumber: Int)

    fun signUpUser(username: String, contestNumber: Int)

    fun getUpcomingContests(username: String): List<UpcomingContest>

    fun getParticipantsByAscendingRanking(contestNumber: Int): List<ParticipantDto>

    fun getParticipant(contestNumber: Int, username: String) : Int
}