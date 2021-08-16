package com.stockcomp.controller

import com.stockcomp.request.CreateContestRequest
import com.stockcomp.response.ContestDto
import com.stockcomp.response.UserDto
import com.stockcomp.service.admin.AdminService
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = ["http://localhost:8000"], allowCredentials = "true")
class AdminController(
    private val adminService: AdminService
) {

    @GetMapping("/contests")
    fun getRunningAndUpcomingContests(): ResponseEntity<List<ContestDto>> {
        val contests = adminService.getRunningAndUpcomingContests()

        return createListResponse(contests)
    }

    @GetMapping("/contests/{id}")
    fun getContest(@PathVariable id: Long): ResponseEntity<ContestDto> {
        val contest = adminService.getContest(id)

        return ResponseEntity.ok(contest)
    }

    @PostMapping("/contests")
    fun createContest(@RequestBody request: CreateContestRequest): ResponseEntity<ContestDto> {
        val contest = adminService.createContest(request)

        return ResponseEntity.ok(contest)
    }

    @PutMapping("/contests/{id}")
    fun updateContest(@RequestBody contestDto: ContestDto): ResponseEntity<ContestDto> {
        val contest = adminService.updateContest(contestDto)

        return ResponseEntity.ok(contest)
    }

    @DeleteMapping("/contests/{id}")
    fun deleteContest(@PathVariable id: Long): ResponseEntity<ContestDto> {
        val contest = adminService.deleteContest(id)

        return ResponseEntity.ok(contest)
    }

    @GetMapping("/users")
    fun getUsers(): HttpEntity<List<UserDto>> {
        val users = adminService.getUsers()

        return createListResponse(users)
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