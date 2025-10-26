package com.stockcomp.participant.internal

import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ParticipantRepository : JpaRepository<Participant, Long> {
    fun findByParticipantId(participantId: Long): Participant

    fun findByUserIdAndContestId(
        userId: Long,
        contestId: Long,
    ): Participant?

    fun findAllByContestId(contestId: Long): List<Participant>

    fun existsByUserIdAndContestId(
        userId: Long,
        contestId: Long,
    ): Boolean

    fun countByContestId(contestId: Long): Long

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Participant p where p.participantId = ?1")
    fun findByIdLocked(participantId: Long): Participant

    fun findAllByContestId(
        contestId: Long,
        pageable: Pageable,
    ): Page<Participant>

    fun findAllByUserId(userId: Long): List<Participant>
}
