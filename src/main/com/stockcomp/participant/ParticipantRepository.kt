package com.stockcomp.participant

import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.participant.entity.Participant
import com.stockcomp.user.entity.User
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ParticipantRepository : JpaRepository<Participant, Long> {

    @Query("SELECT p FROM Participant p inner join p.user u where u.email = ?1 and p.contest = ?2")
    fun findByEmailAndContest(username: String, contest: Contest): List<Participant>

    @Query("SELECT p FROM Participant  p where p.contest = ?1")
    fun findAllByContest(contest: Contest): List<Participant>

    @Query("SELECT p FROM Participant p join p.contest c where c.contestStatus  = ?1")
    fun findAllByContestStatus(contestStatus: ContestStatus): List<Participant>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Participant  p where p.id = ?1")
    fun findByIdLocked(id: Long): Participant

    fun findAllByUser(user: User): List<Participant>

    fun findAllByContest(contest: Contest, request: PageRequest): Page<Participant>

    fun findByContestAndUser(contest: Contest, user: User): Participant?
}