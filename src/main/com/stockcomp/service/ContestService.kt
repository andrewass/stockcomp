package com.stockcomp.service

import com.stockcomp.entity.contest.Contest
import com.stockcomp.repository.ContestRepository
import com.stockcomp.request.CreateContestRequest
import org.slf4j.LoggerFactory
import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.stereotype.Service

@Service
class ContestService(
    private val endpointRegistry: KafkaListenerEndpointRegistry,
    private val contestRepository: ContestRepository
) {
    private val logger = LoggerFactory.getLogger(ContestService::class.java)
    private var contestIsActive = false

    fun createContest(request: CreateContestRequest) {
        val contest = Contest(
            sequenceNumber = request.sequenceNumber,
            startTime = request.startTime
        )
        contestRepository.save(contest)
    }

    fun startContest() {
        if (!contestIsActive) {
            logger.info("Starting contest")
            val contest = contestRepository.findByInPreStartModeIsTrue().get()
            contest.apply {
                inPreStartMode = false
                inRunningMode = true
            }
            contestIsActive = true
            Thread(Task()).start()
            startKafkaConsumersForContest()
        }
    }

    fun stopContest() {
        logger.info("Stopping contest")
        contestIsActive = false
        stopKafkaConsumersForContest()
        val activeContest = contestRepository.findByInPreStartModeIsTrue()
    }

    private fun startKafkaConsumersForContest() {
        endpointRegistry.allListenerContainers.forEach { it.start() }
    }

    private fun stopKafkaConsumersForContest() {
        endpointRegistry.allListenerContainers.forEach { it.stop() }
    }

    private inner class Task : Runnable {
        override fun run() {
            while (contestIsActive) {
                try {
                    Thread.sleep(10000)
                    logger.info("Running the contest")
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }
}