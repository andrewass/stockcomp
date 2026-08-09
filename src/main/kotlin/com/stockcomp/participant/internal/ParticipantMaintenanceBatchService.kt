package com.stockcomp.participant.internal

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ParticipantMaintenanceBatchService(
    private val participantRepository: ParticipantRepository,
    private val participantMaintenanceCursorRepository: ParticipantMaintenanceCursorRepository,
) {
    @Transactional(readOnly = true)
    fun getNextParticipantIds(
        jobName: String,
        contestIds: List<Long>,
        maxParticipants: Int,
    ): List<Long> {
        if (contestIds.isEmpty()) {
            return emptyList()
        }

        val cursor = participantMaintenanceCursorRepository.findById(jobName).orElse(null)?.lastParticipantId()
        val afterCursor =
            cursor?.let { participantId ->
                participantRepository.findParticipantIdsAfter(
                    contestIds = contestIds,
                    afterParticipantId = participantId,
                    pageable = PageRequest.of(0, maxParticipants),
                )
            } ?: participantRepository.findParticipantIdsUpTo(
                contestIds = contestIds,
                upToParticipantId = Long.MAX_VALUE,
                pageable = PageRequest.of(0, maxParticipants),
            )

        if (cursor == null || afterCursor.size == maxParticipants) {
            return afterCursor
        }

        return afterCursor +
            participantRepository.findParticipantIdsUpTo(
                contestIds = contestIds,
                upToParticipantId = cursor,
                pageable = PageRequest.of(0, maxParticipants - afterCursor.size),
            )
    }

    @Transactional
    fun advanceCursor(
        jobName: String,
        lastParticipantId: Long,
    ) {
        val cursor =
            participantMaintenanceCursorRepository
                .findById(jobName)
                .orElseGet { ParticipantMaintenanceCursor(jobName) }
        cursor.advanceTo(lastParticipantId)
        participantMaintenanceCursorRepository.save(cursor)
    }
}
