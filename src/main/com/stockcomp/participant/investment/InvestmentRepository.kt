package com.stockcomp.participant.investment

import com.stockcomp.contest.domain.ContestStatus
import com.stockcomp.participant.entity.Participant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface InvestmentRepository : JpaRepository<Investment, Long> {

    fun findAllByParticipant(participant: Participant): List<Investment>

    @Query("SELECT i FROM Investment i join i.participant p join p.contest c where c.contestStatus  = ?1")
    fun findAllByContestStatus(contestStatus: ContestStatus): List<Investment>
}