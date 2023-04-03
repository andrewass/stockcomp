package com.stockcomp.participant.repository

import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.participant.entity.Participant
import com.stockcomp.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ParticipantRepository : JpaRepository<Participant, Long> {

    @Query("SELECT p FROM Participant p inner join p.user u where u.email = ?1 and p.contest = ?2")
    fun findAllByEmailAndContest(username: String, contest: Contest): List<Participant>

    @Query("SELECT p FROM Participant  p where p.contest = ?1")
    fun findAllByContest(contest: Contest): List<Participant>

    @Query("SELECT p FROM Participant p join p.contest c where c.contestStatus  = ?1")
    fun findAllByContestStatus(contestStatus: ContestStatus): List<Participant>

    fun findAllByUser(user: User): List<Participant>

    fun findAllByContestOrderByTotalValueDesc(contest: Contest): List<Participant>

    fun findAllByContestOrderByRankAsc(contest: Contest): List<Participant>

    fun findAllByContest(contest: Contest, request: PageRequest) : Page<Participant>

    fun findByContestAndUser(contest: Contest, user: User): Participant?
}