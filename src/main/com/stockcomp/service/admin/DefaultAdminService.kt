package com.stockcomp.service.admin

import com.fasterxml.jackson.databind.ObjectMapper
import com.stockcomp.repository.ContestRepository
import com.stockcomp.response.ContestDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultAdminService(
    private val contestRepository: ContestRepository,
    private val objectMapper: ObjectMapper = ObjectMapper()
) : AdminService {

    override fun getRunningAndUpcomingContests(): List<ContestDto> {
        val contests = contestRepository.findAllByInRunningModeIsTrueOrInPreStartModeIsTrue()

        return contests
            .map { objectMapper.convertValue(it, ContestDto::class.java) }
    }
}