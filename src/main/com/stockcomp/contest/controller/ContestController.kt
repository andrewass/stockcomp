package com.stockcomp.contest.controller

import com.stockcomp.authentication.controller.getAccessTokenFromCookie
import com.stockcomp.authentication.service.JwtService
import com.stockcomp.contest.dto.ContestDto
import com.stockcomp.contest.dto.CreateContestRequest
import com.stockcomp.contest.dto.UpdateContestRequest
import com.stockcomp.contest.entity.ContestStatus
import com.stockcomp.contest.service.ContestService
import com.stockcomp.contest.service.mapToContestDto
import com.stockcomp.exception.InvalidRoleException
import com.stockcomp.exception.handler.CustomExceptionHandler
import com.stockcomp.user.entity.Role
import com.stockcomp.user.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/contest")
class ContestController(
    private val userRepository: UserRepository,
    private val contestService: ContestService,
    private val defaultJwtService: JwtService
) : CustomExceptionHandler() {

    @PostMapping("/get-by-status")
    fun getContestsByStatus(@RequestBody statusList: List<ContestStatus>): ResponseEntity<List<ContestDto>> =
        contestService.getContests(statusList)
            .map { mapToContestDto(it) }
            .let { ResponseEntity.ok(it) }


    @GetMapping("/get-by-number")
    fun getContest(@RequestParam contestNumber: Int): ResponseEntity<ContestDto> =
        contestService.getContest(contestNumber)
            .let { ResponseEntity.ok(mapToContestDto(it)) }


    @PostMapping("/create")
    fun createContest(
        servletRequest: HttpServletRequest,
        @RequestBody contestRequest: CreateContestRequest
    ): ResponseEntity<HttpStatus> =
        verifyUserIsAdmin(extractUsernameFromRequest(servletRequest))
            .also { contestService.createContest(contestRequest) }
            .let { ResponseEntity(HttpStatus.OK) }


    @PutMapping("/update")
    fun updateContest(
        servletRequest: HttpServletRequest,
        @RequestBody contestRequest: UpdateContestRequest
    ): ResponseEntity<HttpStatus> =
        verifyUserIsAdmin(extractUsernameFromRequest(servletRequest))
            .also { contestService.updateContest(contestRequest) }
            .let { ResponseEntity(HttpStatus.OK) }


    @DeleteMapping("/delete")
    fun deleteContest(
        servletRequest: HttpServletRequest,
        @RequestParam contestNumber: Int
    ): ResponseEntity<HttpStatus> =
        verifyUserIsAdmin(extractUsernameFromRequest(servletRequest))
            .also { contestService.deleteContest(contestNumber) }
            .let { ResponseEntity(HttpStatus.OK) }


    private fun extractUsernameFromRequest(servletRequest: HttpServletRequest): String =
        getAccessTokenFromCookie(servletRequest)
            .let { defaultJwtService.extractUsername(it!!) }


    private fun verifyUserIsAdmin(username: String) {
        userRepository.findByUsername(username).let {
            if (it.userRole != Role.ADMIN) {
                throw InvalidRoleException("Admin role is required. Found role : ${it.userRole}")
            }
        }
    }
}