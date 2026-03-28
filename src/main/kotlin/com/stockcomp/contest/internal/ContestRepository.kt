package com.stockcomp.contest.internal

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContestRepository : JpaRepository<Contest, Long> {
    fun findAllByContestStatusIn(contestStatusList: List<ContestStatus>): List<Contest>

    fun existsByContestStatusIn(contestStatusList: List<ContestStatus>): Boolean
}
