package com.stockcomp.participant.internal

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ParticipantMaintenanceCursorRepository : JpaRepository<ParticipantMaintenanceCursor, String>
