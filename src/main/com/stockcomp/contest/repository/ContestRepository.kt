package com.stockcomp.contest.repository

import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ContestRepository : JpaRepository<Contest, Long> {

    fun findByContestNumber(contestNumber: Int): Contest

    fun findAllByContestStatusIn(contestStatusList: List<ContestStatus>): List<Contest>

    @Query(
        value = "SELECT C.* FROM CONTEST C" +
                "WHERE C.CONTEST_STATUS IN ('AWAITING','RUNNING','STOPPED')" +
                "   AND NOT EXISTS (" +
                "       SELECT 1 FROM PARTICIPANT P" +
                "       WHERE P.CONTEST_ID = C.CONTEST_ID" +
                "           AND P.USER_ID = ?1" +
                ")", nativeQuery = true
    )
    fun getAllActiveContestsNotSignedUp(userId: Long): List<Contest>

    fun deleteByContestNumber(contestNumber: Int)
}