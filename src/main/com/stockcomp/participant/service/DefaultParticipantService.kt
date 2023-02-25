package com.stockcomp.participant.service

import com.stockcomp.contest.entity.Contest
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.service.ContestService
import com.stockcomp.participant.entity.Participant
import com.stockcomp.participant.repository.ParticipantRepository
import com.stockcomp.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultParticipantService(
    private val contestService: ContestService,
    private val participantRepository: ParticipantRepository,
    private val userService: UserService
) : ParticipantService {

    private val logger = LoggerFactory.getLogger(DefaultParticipantService::class.java)

    override fun getActiveParticipantsByUser(username: String): List<Participant> =
        contestService.getContests(listOf(ContestStatus.RUNNING, ContestStatus.STOPPED))
            .flatMap { getAllByEmailAndContest(username, it) }


    override fun getParticipantsSortedByRank(contestNumber: Int): List<Participant> =
        contestService.findByContestNumber(contestNumber)
            .let { participantRepository.findAllByContestOrderByRankAsc(it) }


    override fun getParticipant(contestNumber: Int, email: String): Participant? =
        participantRepository.findByContestAndUser(
            contestService.findByContestNumber(contestNumber),
            userService.findUserByEmail(email)
        )

    override fun getAllByContest(contest: Contest): List<Participant> =
        participantRepository.findAllByContest(contest)


    override fun getAllByEmailAndContest(email: String, contest: Contest): List<Participant> =
        participantRepository.findAllByEmailAndContest(email, contest)


    override fun getParticipantHistory(username: String): List<Participant> =
        userService.findUserByUsername(username)
            .let { participantRepository.findAllByUser(it) }
            .filter {
                it.contest.contestStatus in listOf(
                    ContestStatus.RUNNING, ContestStatus.STOPPED, ContestStatus.COMPLETED
                )
            }

    override fun signUpParticipant(email: String, contestNumber: Int) {
        val contest = contestService.findByContestNumber(contestNumber)
        assert(
            contest.contestStatus in listOf(
                ContestStatus.RUNNING, ContestStatus.STOPPED, ContestStatus.AWAITING_START
            )
        )
        Participant(
            user = userService.findUserByEmail(email),
            contest = contest,
            rank = contest.participantCount + 1
        ).also { participantRepository.save(it) }

        contest.participantCount++
        contestService.saveContest(contest)
    }

    override fun saveParticipant(participant: Participant) {
        participantRepository.save(participant)
    }

    override fun maintainParticipantValues(contest: Contest) {
        logger.info("Maintaining participant values for contest number : ${contest.contestNumber}")
        getAllByContest(contest)
            .forEach { participant ->
                participant.updateInvestmentValues()
            }
    }
}