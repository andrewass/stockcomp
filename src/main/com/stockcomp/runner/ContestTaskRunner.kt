package com.stockcomp.runner

import com.stockcomp.domain.contest.enums.ContestStatus
import com.stockcomp.repository.ContestRepository
import com.stockcomp.service.investment.MaintainParticipantsService
import com.stockcomp.service.order.DefaultOrderProcessingService
import com.stockcomp.service.order.OrderProcessingService
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class ContestTaskRunner(
    private val orderProcessingService: OrderProcessingService,
    private val maintainParticipantsService: MaintainParticipantsService,
    private val contestRepository: ContestRepository
) : ApplicationRunner {

    private val logger = LoggerFactory.getLogger(DefaultOrderProcessingService::class.java)

    override fun run(args: ApplicationArguments?) {
        logger.info("Starting ContestTaskRunner")
        if (contestRepository.findAllByContestStatus(ContestStatus.RUNNING).isNotEmpty()) {
            orderProcessingService.startOrderProcessing()
            maintainParticipantsService.startParticipantsMaintenance()
        }
    }
}