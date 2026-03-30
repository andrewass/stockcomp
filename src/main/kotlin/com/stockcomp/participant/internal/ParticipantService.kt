package com.stockcomp.participant.internal

import com.stockcomp.contest.ContestDto
import com.stockcomp.contest.ContestServiceExternal
import com.stockcomp.participant.ContestParticipantDto
import com.stockcomp.participant.DetailedParticipantDto
import com.stockcomp.participant.mapToInvestmentDto
import com.stockcomp.participant.mapToInvestmentOrderDto
import com.stockcomp.participant.toUserParticipantDto
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
    private val participantRepository: ParticipantRepository,
) {
    fun signUpParticipant(
        userId: Long,
        contestId: Long,
    ): Participant {
        contestService.getContest(contestId)
        if (participantRepository.existsByUserIdAndContestId(userId, contestId)) {
            throw IllegalStateException("User $userId is already signed up for contest $contestId")
        }
        return participantRepository.save(
            Participant(
                userId = userId,
                contestId = contestId,
            ),
        )
    }

    fun getParticipatingContests(userId: Long): List<ContestParticipantDto> =
        contestService.getActiveContests().mapNotNull { contest ->
            participantRepository
                .findByUserIdAndContestId(userId, contest.contestId)
                ?.let { participant ->
                    ContestParticipantDto(toUserParticipantDto(participant), contest)
                }
        }

    fun getNonParticipatingContests(userId: Long): List<ContestDto> =
        contestService
            .getActiveContests()
            .filter { !participantRepository.existsByUserIdAndContestId(userId, it.contestId) }

    fun getDetailedParticipantsForSymbol(
        userId: Long,
        symbol: String,
    ): List<DetailedParticipantDto> {
        val normalizedSymbol = symbol.trim().uppercase()
        return contestService
            .getRunningContests()
            .mapNotNull { contest ->
                participantRepository
                    .findByUserIdAndContestId(userId = userId, contestId = contest.contestId)
                    ?.let { participant ->
                        DetailedParticipantDto(
                            contest = contest,
                            participant = toUserParticipantDto(participant),
                            investments = participant.getInvestmentsForSymbol(normalizedSymbol).map { mapToInvestmentDto(it) },
                            completedOrders =
                                participant
                                    .getCompletedInvestmentOrdersForSymbol(normalizedSymbol)
                                    .map { mapToInvestmentOrderDto(it) },
                            activeOrders =
                                participant
                                    .getActiveInvestmentOrdersForSymbol(normalizedSymbol)
                                    .map { mapToInvestmentOrderDto(it) },
                        )
                    }
            }
    }

    fun getDetailedParticipantForContest(
        contestId: Long,
        userId: Long,
    ): DetailedParticipantDto? {
        val contest = contestService.getContest(contestId)
        return participantRepository
            .findByUserIdAndContestId(userId, contestId)
            ?.let {
                DetailedParticipantDto(
                    contest = contest,
                    participant = toUserParticipantDto(it),
                    investments = it.investments().map { investment -> mapToInvestmentDto(investment) },
                    completedOrders =
                        it
                            .getCompletedInvestmentOrders()
                            .map { investmentOrder -> mapToInvestmentOrderDto(investmentOrder) },
                    activeOrders =
                        it
                            .getActiveInvestmentOrders()
                            .map { investmentOrder -> mapToInvestmentOrderDto(investmentOrder) },
                )
            }
    }

    fun getParticipantsSortedByRank(
        contestId: Long,
        pageNumber: Int,
        pageSize: Int,
    ): Page<Participant> =
        participantRepository.findAllByContestId(
            contestId = contestId,
            pageable = PageRequest.of(pageNumber, pageSize, Sort.by("rank")),
        )

    fun getParticipant(
        contestId: Long,
        userId: Long,
    ): Participant =
        participantRepository.findByUserIdAndContestId(userId = userId, contestId = contestId)
            ?: throw NoSuchElementException("Participant for user $userId in contest $contestId was not found")

    fun getAllByContest(contestId: Long): List<Participant> = participantRepository.findAllByContestId(contestId)

    fun getParticipantHistory(username: String): List<Participant> {
        val userId = userService.getUserIdByUsername(username)
        return participantRepository
            .findAllByUserId(userId)
            .filter { contestService.isCompletedContest(it.contestId) }
    }

    fun getParticipantByIdAndUserId(
        participantId: Long,
        userId: Long,
    ): Participant =
        participantRepository
            .findByParticipantIdAndUserId(participantId = participantId, userId = userId)
            ?: throw NoSuchElementException("Participant $participantId was not found for user $userId")

    fun findOptionalParticipant(
        contestId: Long,
        userId: Long,
    ): Participant? = participantRepository.findByUserIdAndContestId(userId = userId, contestId = contestId)

    fun getParticipantByIdLocked(participantId: Long): Participant = participantRepository.findByIdLocked(participantId)

    fun saveParticipant(participant: Participant): Participant = participantRepository.save(participant)
}
