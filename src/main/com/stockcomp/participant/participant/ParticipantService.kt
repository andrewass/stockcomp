package com.stockcomp.participant.participant

import com.stockcomp.contest.ContestDto
import com.stockcomp.contest.ContestServiceExternal
import com.stockcomp.participant.investment.mapToInvestmentDto
import com.stockcomp.participant.investmentorder.mapToInvestmentOrderDto
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

    fun getParticipatingContests(userId: Long): List<ContestParticipantDto> =
        contestService.getActiveContests().mapNotNull { contest ->
            participantRepository.findByUserIdAndContestId(userId, contest.contestId)
                ?.let { participant ->
                    ContestParticipantDto(toUserParticipantDto(participant), contest)
                }
        }

    fun getUnregisteredContests(userId: Long): List<ContestDto> =
        contestService.getActiveContests()
            .filter { !participantRepository.existsByUserIdAndContestId(userId, it.contestId) }

    fun getDetailedParticipantsForSymbol(userId: Long, symbol: String): List<DetailedParticipantDto> =
        contestService.getRunningContests()
            .mapNotNull { getOptionalParticipant(it.contestId, userId) }
            .map { participant ->
                DetailedParticipantDto(
                    contest = contestService.getContest(participant.contestId),
                    participant = toUserParticipantDto(participant),
                    investments = participant.getInvestmentsForSymbol(symbol).map { mapToInvestmentDto(it) },
                    completedOrders = participant.getCompletedInvestmentOrdersForSymbol(symbol)
                        .map { mapToInvestmentOrderDto(it) },
                    activeOrders = participant.getActiveInvestmentOrders()
                        .map { mapToInvestmentOrderDto(it) }
                )
            }

    fun getDetailedParticipantForContest(contestId: Long, userId: Long): DetailedParticipantDto? {
        val contest = contestService.getContest(contestId)
        return participantRepository.findByUserIdAndContestId(userId, contestId)
            ?.let {
                DetailedParticipantDto(
                    contest = contest,
                    participant = toUserParticipantDto(it),
                    investments = it.investments.map { investment -> mapToInvestmentDto(investment) },
                    completedOrders = it.getCompletedInvestmentOrders()
                        .map { investmentOrder -> mapToInvestmentOrderDto(investmentOrder) },
                    activeOrders = it.getActiveInvestmentOrders()
                        .map { investmentOrder -> mapToInvestmentOrderDto(investmentOrder) }
                )
            }
    }

    fun getParticipantsSortedByRank(contestId: Long, pageNumber: Int, pageSize: Int): Page<Participant> =
        participantRepository.findAllByContestId(
            contestId = contestId,
            pageable = PageRequest.of(pageNumber, pageSize, Sort.by("rank"))
        )

    fun getParticipant(contestId: Long, userId: Long): Participant =
        participantRepository.findByUserIdAndContestId(userId = userId, contestId = contestId)!!

    fun getParticipant(participantId: Long): Participant =
        participantRepository.findByParticipantId(participantId)


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
