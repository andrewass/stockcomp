package com.stockcomp.participant

import com.stockcomp.contest.ContestServiceExternal
import com.stockcomp.participant.dto.DetailedParticipantDto
import com.stockcomp.participant.dto.mapToDetailedParticipant
import com.stockcomp.participant.entity.Participant
import com.stockcomp.user.UserServiceExternal
import org.slf4j.LoggerFactory
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

    fun getActiveParticipants(userId: Long): List<Participant> =
        contestService.getActiveContests()
            .mapNotNull { getParticipant(userId = userId, contestId = it) }

    fun getRunningDetailedParticipantsForSymbol(userId: Long, symbol: String): List<DetailedParticipantDto> =
        contestService.getRunningContests()
            .mapNotNull { getParticipant(userId = userId, contestId = it) }
            .map { mapToDetailedParticipant(source = it, symbol = symbol) }

    fun getParticipantsSortedByRank(contestId: Long, pageNumber: Int, pageSize: Int): Page<Participant> =
        participantRepository.findAllByContestId(
            contestId,
            request = PageRequest.of(pageNumber, pageSize, Sort.by("rank"))
        )

    fun getParticipant(contestId: Long, userId: Long): Participant? =
        participantRepository.findByUserIdAndContestId(userId = userId, contestId = contestId)
            .firstOrNull()

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
}