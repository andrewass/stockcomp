package com.stockcomp.contest.service

import com.stockcomp.contest.dto.CreateContestRequest
import com.stockcomp.contest.dto.UpdateContestRequest
import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus

interface ContestService {

    fun getContest(contestNumber: Int): Contest

    fun createContest(request: CreateContestRequest)

    fun deleteContest(contestNumber: Int)

    fun updateContest(request: UpdateContestRequest)

    fun startContest(contestNumber: Int)

    fun stopContest(contestNumber: Int)

    fun completeContest(contestNumber: Int)

    fun getContests(statusList: List<ContestStatus>): List<Contest>

    fun findByContestNumber(contestNumber: Int): Contest

    fun saveContest(contest: Contest)
}