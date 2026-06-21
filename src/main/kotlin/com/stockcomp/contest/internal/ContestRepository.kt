package com.stockcomp.contest.internal

import com.stockcomp.contest.ContestStatus
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ContestRepository : JpaRepository<Contest, Long> {
    @Query("SELECT c FROM Contest c WHERE c._contestStatus IN :contestStatusList")
    fun findAllByContestStatusIn(contestStatusList: List<ContestStatus>): List<Contest>

    @Query("SELECT COUNT(c) > 0 FROM Contest c WHERE c._contestStatus IN :contestStatusList")
    fun existsByContestStatusIn(contestStatusList: List<ContestStatus>): Boolean

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Contest c WHERE c.contestId = :contestId")
    fun findByIdForUpdate(contestId: Long): Contest?
}
