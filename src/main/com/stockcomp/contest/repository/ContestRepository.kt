package com.stockcomp.contest.repository

import com.stockcomp.contest.domain.Contest
import com.stockcomp.contest.domain.ContestStatus
import com.stockcomp.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ContestRepository : JpaRepository<Contest, Long> {

    fun findByContestNumber(contestNumber: Int): Contest

    fun findAllByContestStatusIn(contestStatusList: List<ContestStatus>): List<Contest>

    @Query("select c from Contest c inner join Participant p on p.contest = c and p.user  = ?1")
    fun getAllActiveContestsSignedUp(user: User): List<Contest>

    @Query(
        value = "SELECT C.* FROM T_CONTEST C " +
                "WHERE C.CONTEST_STATUS IN ('AWAITING_START','RUNNING','STOPPED') " +
                "   AND NOT EXISTS (" +
                "       SELECT 1 FROM T_PARTICIPANT P" +
                "       WHERE P.CONTEST_ID = C.CONTEST_ID" +
                "           AND P.USER_ID = ?1" +
                ")", nativeQuery = true
    )
    fun getAllActiveContestsNotSignedUp(userId: Long): List<Contest>

    fun deleteByContestNumber(contestNumber: Int)
}