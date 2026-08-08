package com.stockcomp.contest

interface ContestServiceExternal {
    fun getContest(contestId: Long): ContestDto

    fun getRunningContests(): List<ContestDto>

    fun getActiveContests(): List<ContestDto>

    fun getContestsAwaitingCompletion(): List<ContestDto>

    fun requireContestAllowsSignUp(contestId: Long)

    fun requireContestIsRunning(contestId: Long)

    fun isCompletedContest(contestId: Long): Boolean

    fun markContestAsCompleted(contestId: Long)

    fun lockContestForCompletion(contestId: Long): Boolean
}
