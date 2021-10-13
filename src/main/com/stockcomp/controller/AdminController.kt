package com.stockcomp.controller

import com.stockcomp.request.CreateContestRequest
import com.stockcomp.response.ContestDto
import com.stockcomp.response.UserDto
import com.stockcomp.service.admin.AdminService
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = ["http://localhost:8000"], allowCredentials = "true")
@Api(description = "Endpoints available for admin users")
class AdminController(
    private val adminService: AdminService
) {

    @GetMapping("/contests")
    @ApiOperation(value = "Get running and upcoming contests")
    fun getRunningAndUpcomingContests(): ResponseEntity<List<ContestDto>> {
        val contests = adminService.getRunningAndUpcomingContests()

        return createListResponse(contests)
    }

    @GetMapping("/contests/{id}")
    @ApiOperation(value = "Get contest by its ID")
    fun getContest(@PathVariable id: Long): ResponseEntity<ContestDto> {
        val contest = adminService.getContest(id)

        return ResponseEntity.ok(contest)
    }

    @PostMapping("/contests")
    @ApiOperation(value = "Create a new contest")
    fun createContest(@RequestBody request: CreateContestRequest): ResponseEntity<ContestDto> {
        val contest = adminService.createContest(request)

        return ResponseEntity.ok(contest)
    }

    @PutMapping("/contests/{id}")
    @ApiOperation(value = "Update status of an existing contest")
    fun updateContest(@RequestBody contestDto: ContestDto): ResponseEntity<ContestDto> {
        val contest = adminService.updateContest(contestDto)

        return ResponseEntity.ok(contest)
    }

    @DeleteMapping("/contests/{id}")
    @ApiOperation(value = "Delete an existing contest, specified by its ID")
    fun deleteContest(@PathVariable id: Long): ResponseEntity<ContestDto> {
        val contest = adminService.deleteContest(id)

        return ResponseEntity.ok(contest)
    }

    @GetMapping("/users")
    @ApiOperation(value = "Get all signed-up users")
    fun getUsers(): HttpEntity<List<UserDto>> {
        val users = adminService.getUsers()

        return createListResponse(users)
    }

    @PostMapping("/update-leaderboard/{contestNumber}")
    @ApiOperation(value = "Update the leaderboard based on a given contest number")
    fun updateLeaderboard(@PathVariable contestNumber : Int) : ResponseEntity<HttpStatus> {
        adminService.updateLeaderboard(contestNumber)

        return ResponseEntity(HttpStatus.OK)
    }

    private fun <T : Any> createListResponse(result: List<T>): ResponseEntity<List<T>> {
        val responseHeader = HttpHeaders()
        responseHeader.set("Access-Control-Expose-Headers", "Content-Range")
        responseHeader.set("Content-Range", "posts 0-${result.size}/${result.size}")

        return ResponseEntity.ok()
            .headers(responseHeader)
            .body(result)
    }
}