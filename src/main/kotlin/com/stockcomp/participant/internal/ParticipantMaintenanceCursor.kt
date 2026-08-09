package com.stockcomp.participant.internal

import com.stockcomp.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "T_PARTICIPANT_MAINTENANCE_CURSOR")
class ParticipantMaintenanceCursor(
    @Id
    @Column(name = "JOB_NAME", nullable = false, length = 64)
    val jobName: String,
) : BaseEntity() {
    @Column(name = "LAST_PARTICIPANT_ID")
    private var lastParticipantId: Long? = null

    fun lastParticipantId(): Long? = lastParticipantId

    fun advanceTo(participantId: Long) {
        lastParticipantId = participantId
    }
}
