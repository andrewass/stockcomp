package com.stockcomp.service.contest

import com.stockcomp.response.UpcomingContest

interface ContestService {

    fun startContest(contestNumber: Int)

    fun stopContest(contestNumber: Int)

    fun completeContest(contestNumber: Int)

    fun signUpUser(username: String, contestNumber: Int)

    fun getUpcomingContests(username: String): List<UpcomingContest>

    fun userIsParticipating(username: String, contestNumber: Int) : Boolean
}