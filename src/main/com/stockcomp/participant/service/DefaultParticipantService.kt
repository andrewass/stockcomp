package com.stockcomp.participant.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.service.ContestService
import com.stockcomp.participant.entity.Participant
import com.stockcomp.participant.repository.ParticipantRepository
import com.stockcomp.user.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultParticipantService(
    private val contestService: ContestService,
    private val participantRepository: ParticipantRepository,
    private val userService: UserService
) : ParticipantService {

    override fun getParticipantsSortedByRank(contestNumber: Int): List<Participant> =
        contestService.findByContestNumber(contestNumber)
            .let { participantRepository.findAllByContestOrderByRankAsc(it) }


    override fun getParticipant(contestNumber: Int, username: String): Participant? =
        participantRepository.findByContestAndUser(
            contestService.findByContestNumber(contestNumber),
            userService.findUserByUsername(username)
        )

    override fun getAllByContest(contest: Contest): List<Participant> =
        participantRepository.findAllByContest(contest)


    override fun getAllByUsernameAndContest(username: String, contest: Contest) : List<Participant> =
        participantRepository.findAllByUsernameAndContest(username, contest)


    override fun getParticipantHistory(username: String): List<Participant> =
        userService.findUserByUsername(username)
            .let { participantRepository.findAllByUser(it) }
            .filter {
                it.contest.contestStatus in listOf(
                    ContestStatus.RUNNING,
                    ContestStatus.STOPPED,
                    ContestStatus.COMPLETED
                )
            }


    override fun signUpParticipant(username: String, contestNumber: Int) {
        val contest = contestService.findByContestNumber(contestNumber)
        assert(
            contest.contestStatus in listOf(
                ContestStatus.RUNNING,
                ContestStatus.STOPPED,
                ContestStatus.AWAITING_START
            )
        )
        Participant(
            user = userService.findUserByUsername(username)!!,
            contest = contest,
            rank = contest.participantCount + 1
        ).also { participantRepository.save(it) }

        contest.participantCount++
        contestService.saveContest(contest)
    }

    override fun saveParticipant(participant: Participant) {
        participantRepository.save(participant)
    }
}