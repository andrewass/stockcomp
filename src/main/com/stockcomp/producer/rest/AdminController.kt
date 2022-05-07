package com.stockcomp.producer.rest

import com.stockcomp.contest.dto.ContestDto
import com.stockcomp.dto.user.UserDetailsDto
import com.stockcomp.contest.dto.CreateContestRequest
import com.stockcomp.contest.dto.UpdateContestRequest
import com.stockcomp.service.admin.AdminService
import com.stockcomp.tasks.ContestTasks
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin")
class AdminController(
    private val adminService: AdminService,
    private val contestTasks: ContestTasks
) {

    @GetMapping("/contests")
    fun getRunningAndUpcomingContests(): ResponseEntity<List<ContestDto>> =
        createListResponse(adminService.getAllContests())


    @GetMapping("/contests/{id}")
    fun getContest(@PathVariable id: Long): ResponseEntity<ContestDto> =
        ResponseEntity.ok(adminService.getContest(id))


    @PostMapping("/contests")
    fun createContest(@RequestBody request: CreateContestRequest): ResponseEntity<ContestDto> =
        ResponseEntity.ok(adminService.createContest(request))


    @PutMapping("/contests/{id}")
    fun updateContest(@RequestBody request: UpdateContestRequest): ResponseEntity<ContestDto> =
        ResponseEntity.ok(adminService.updateContestStatus(request))


    @DeleteMapping("/contests/{id}")
    fun deleteContest(@PathVariable id: Long): ResponseEntity<ContestDto> =
        ResponseEntity.ok(adminService.deleteContest(id))


    @GetMapping("/users")
    fun getUsers(): HttpEntity<List<UserDetailsDto>> =
        createListResponse(adminService.getUsers())


    @PostMapping("/start-contest-tasks")
    fun startContestTasks(): ResponseEntity<HttpStatus> =
        contestTasks.startContestTasks()
            .run { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/stop-contest-tasks")
    fun stopContestTasks(): ResponseEntity<HttpStatus> =
        contestTasks.stopContestTasks()
            .run { ResponseEntity(HttpStatus.OK) }


    @PostMapping("/complete-contest-tasks")
    fun completeContestTasks(@RequestParam("contestNumber") contestNumber: Int): ResponseEntity<HttpStatus> =
        contestTasks.completeContestTasks(contestNumber)
            .run { ResponseEntity(HttpStatus.OK) }


    private fun <T : Any> createListResponse(result: List<T>): ResponseEntity<List<T>> =
        HttpHeaders().apply {
            this.set("Access-Control-Expose-Headers", "Content-Range")
            this.set("Content-Range", "posts 0-${result.size}/${result.size}")
        }.let { ResponseEntity.ok().headers(it).body(result) }
}