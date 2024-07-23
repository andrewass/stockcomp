package com.stockcomp.participant

import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ParticipantRepository : JpaRepository<Participant, Long> {

    fun findByUserIdAndContestId(userId: Long, contestId: Long): List<Participant>

    fun findAllByContestId(contestId: Long): List<Participant>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Participant p where p.participantId = ?1")
    fun findByIdLocked(participantId: Long): Participant

    fun findAllByContestId(contestId: Long, request: PageRequest): Page<Participant>

    fun findAllByUserId(userId: Long): List<Participant>
}