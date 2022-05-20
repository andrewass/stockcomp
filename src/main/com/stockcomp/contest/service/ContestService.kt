package com.stockcomp.contest.service

import com.stockcomp.contest.dto.ContestDto
import com.stockcomp.contest.dto.CreateContestRequest
import com.stockcomp.contest.dto.UpdateContestRequest
import com.stockcomp.contest.entity.ContestStatus

interface ContestService {

    fun getContest(contestNumber: Int): ContestDto

    fun createContest(request: CreateContestRequest)

    fun deleteContest(contestNumber: Int)

    fun updateContest(updateContestRequest: UpdateContestRequest)

    fun startContest(contestNumber: Int)

    fun stopContest(contestNumber: Int)

    fun completeContest(contestNumber: Int)

    fun signUp(username: String, contestNumber: Int)

    fun getContests(statusList: List<ContestStatus>): List<ContestDto>
}