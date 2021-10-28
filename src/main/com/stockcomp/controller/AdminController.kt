package com.stockcomp.controller

import com.stockcomp.dto.ContestDto
import com.stockcomp.dto.UserDto
import com.stockcomp.request.CreateContestRequest
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


    private fun <T : Any> createListResponse(result: List<T>): ResponseEntity<List<T>> =
        HttpHeaders().apply {
            this.set("Access-Control-Expose-Headers", "Content-Range")
            this.set("Content-Range", "posts 0-${result.size}/${result.size}")
        }.let { ResponseEntity.ok().headers(it).body(result) }
}