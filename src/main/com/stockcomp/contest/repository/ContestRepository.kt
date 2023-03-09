package com.stockcomp.contest.repository

import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContestRepository : JpaRepository<Contest, Long> {

    fun findByContestNumber(contestNumber: Int): Contest

    fun findByContestNumberAndContestStatus(contestNumber: Int, contestStatus: ContestStatus): Contest

    fun findAllByContestStatusIn(contestStatusList: List<ContestStatus>): List<Contest>

    fun deleteByContestNumber(contestNumber: Int)
}