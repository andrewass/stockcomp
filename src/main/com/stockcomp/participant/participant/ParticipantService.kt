package com.stockcomp.participant.participant

import com.stockcomp.contest.ContestDto
import com.stockcomp.contest.ContestServiceExternal
import com.stockcomp.user.UserServiceExternal
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ParticipantService(
    private val contestService: ContestServiceExternal,
    private val userService: UserServiceExternal,
    private val participantRepository: ParticipantRepository
) {

    fun signUpParticipant(userId: Long, contestId: Long) {
        participantRepository.save(
            Participant(
                userId = userId,
                contestId = contestId
            )
        )
    }

    fun getRegisteredParticipatingContests(userId: Long): List<ContestParticipantDto> =
        contestService.getActiveContests().mapNotNull { contest ->
            participantRepository.findByUserIdAndContestId(userId, contest.contestId)
                ?.let { participant ->
                    val participantCount = participantRepository.countByContestId(participant.contestId)
                    ContestParticipantDto(toParticipantDto(participant, participantCount), contest)
                }
        }

    fun getUnregisteredContests(userId: Long): List<ContestDto> =
        contestService.getActiveContests()
            .filter { !participantRepository.existsByUserIdAndContestId(userId, it.contestId) }

    fun getRunningDetailedParticipantsForSymbol(userId: Long, symbol: String): List<DetailedParticipantDto> =
        contestService.getRunningContests()
            .mapNotNull { getOptionalParticipant(userId, it.contestId) }
            .map {participant: Participant ->
                val contest = contestService.get
                mapToDetailedParticipant(participant, symbol, )
            }

    fun getParticipantsSortedByRank(contestId: Long, pageNumber: Int, pageSize: Int): Page<Participant> =
        participantRepository.findAllByContestId(
            contestId,
            request = PageRequest.of(pageNumber, pageSize, Sort.by("rank"))
        )

    fun getParticipant(contestId: Long, userId: Long): Participant =
        participantRepository.findByUserIdAndContestId(userId = userId, contestId = contestId)!!

    fun getParticipant(participantId: Long): Participant =
        participantRepository.findById(participantId)
            .orElseThrow { IllegalArgumentException("Partipant Id $participantId not found") }

    fun getLockedParticipant(participantId: Long): Participant =
        participantRepository.findByIdLocked(participantId)

    fun getAllByContest(contestId: Long): List<Participant> =
        participantRepository.findAllByContestId(contestId)

    fun getParticipantHistory(username: String): List<Participant> {
        val userId = userService.getUserIdByUsername(username)
        return participantRepository.findAllByUserId(userId)
            .filter { contestService.isCompletedContest(it.contestId) }
    }

    fun saveParticipant(participant: Participant) {
        participantRepository.save(participant)
    }

    private fun getOptionalParticipant(contestId: Long, userId: Long): Participant? =
        participantRepository.findByUserIdAndContestId(userId = userId, contestId = contestId)

    private fun getParticipantCount(contestId: Long): Long =
        participantRepository.countByContestId(contestId)
}
