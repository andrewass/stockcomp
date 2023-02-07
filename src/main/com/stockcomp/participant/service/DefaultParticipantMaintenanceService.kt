package com.stockcomp.participant.service

import com.stockcomp.contest.service.ContestService
import com.stockcomp.participant.repository.ParticipantRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultParticipantMaintenanceService(
    private val participantRepository: ParticipantRepository,
    private val contestService: ContestService
) : MaintainParticipantService {

    private val logger = LoggerFactory.getLogger(DefaultParticipantMaintenanceService::class.java)
    override fun maintainParticipants() {
        TODO("Not yet implemented")
    }


}