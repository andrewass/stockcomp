package com.stockcomp.contest.service

import com.stockcomp.contest.dto.CreateContestRequest
import com.stockcomp.contest.dto.UpdateContestRequest
import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus

interface ContestService {

    fun createContest(request: CreateContestRequest)

    fun deleteContest(contestNumber: Int)

    fun updateContest(request: UpdateContestRequest)

    fun getContests(statusList: List<ContestStatus>): List<Contest>

    fun findByContestNumber(contestNumber: Int): Contest

    fun findByContestNumberAndStatus(status: ContestStatus, contestNumber: Int): Contest

    fun saveContest(contest: Contest)

    fun getActiveContest(): Contest?
}