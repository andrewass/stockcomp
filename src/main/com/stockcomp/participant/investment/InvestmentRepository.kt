package com.stockcomp.participant.investment

import com.stockcomp.participant.participant.Participant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InvestmentRepository : JpaRepository<Investment, Long> {

    fun findAllByParticipant(participant: Participant): List<Investment>
}