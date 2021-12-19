package com.stockcomp.controller

import com.stockcomp.dto.ContestDto
import com.stockcomp.dto.UserDto
import com.stockcomp.request.CreateContestRequest
import com.stockcomp.service.admin.AdminService
import com.stockcomp.service.contest.ContestService
import com.stockcomp.tasks.ContestTasks
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin")
@Api(description = "Endpoints available for admin users")
class AdminController(
    private val adminService: AdminService,
    private val contestTasks: ContestTasks,
    private val contestService: ContestService
) {

    @GetMapping("/contests")
    @ApiOperation(value = "Get all contests")
    fun getRunningAndUpcomingContests(): ResponseEntity<List<ContestDto>> =
        createListResponse(adminService.getAllContests())


    @GetMapping("/contests/{id}")
    @ApiOperation(value = "Get contest by its ID")
    fun getContest(@PathVariable id: Long): ResponseEntity<ContestDto> =
        ResponseEntity.ok(adminService.getContest(id))


    @PostMapping("/contests")
    @ApiOperation(value = "Create a new contest")
    fun createContest(@RequestBody request: CreateContestRequest): ResponseEntity<ContestDto> =
        ResponseEntity.ok(adminService.createContest(request))


    @PutMapping("/contests/{id}")
    @ApiOperation(value = "Update status of an existing contest")
    fun updateContest(@RequestBody contestDto: ContestDto): ResponseEntity<ContestDto> =
        ResponseEntity.ok(adminService.updateContestStatus(contestDto))


    @DeleteMapping("/contests/{id}")
    @ApiOperation(value = "Delete an existing contest, specified by its ID")
    fun deleteContest(@PathVariable id: Long): ResponseEntity<ContestDto> =
        ResponseEntity.ok(adminService.deleteContest(id))


    @GetMapping("/users")
    @ApiOperation(value = "Get all signed-up users")
    fun getUsers(): HttpEntity<List<UserDto>> =
        createListResponse(adminService.getUsers())


    @PostMapping("/update-leaderboard")
    @ApiOperation(value = "Update leaderboard based on a given contest")
    fun updateLeaderboardFromContest(@RequestParam contestNumber: Int): ResponseEntity<HttpStatus> =
        adminService.updateLeaderboard(contestNumber)
            .run { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/start-contest")
    fun startContest(@RequestParam("contestNumber") contestNumber: Int): ResponseEntity<HttpStatus> =
        contestService.startContest(contestNumber)
            .run { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/stop-contest")
    fun stopContest(@RequestParam("contestNumber") contestNumber: Int): ResponseEntity<HttpStatus> =
        contestService.stopContest(contestNumber)
            .run { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/start-contest-tasks")
    @ApiOperation("Start tasks for processing orders and investments of a contest")
    fun startContestTasks(): ResponseEntity<HttpStatus> =
        contestTasks.startContestTasks()
            .run { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/stop-contest-tasks")
    @ApiOperation("Stop tasks for processing orders and investments of a contest")
    fun stopContestTasks(): ResponseEntity<HttpStatus> =
        contestTasks.stopContestTasks()
            .run { ResponseEntity(HttpStatus.OK) }


    private fun <T : Any> createListResponse(result: List<T>): ResponseEntity<List<T>> =
        HttpHeaders().apply {
            this.set("Access-Control-Expose-Headers", "Content-Range")
            this.set("Content-Range", "posts 0-${result.size}/${result.size}")
        }.let { ResponseEntity.ok().headers(it).body(result) }
}