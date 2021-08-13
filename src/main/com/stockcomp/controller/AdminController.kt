package com.stockcomp.controller

import com.stockcomp.response.ContestDto
import com.stockcomp.service.admin.AdminService
import org.springframework.http.HttpEntity
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = ["http://localhost:8000"], allowCredentials = "true")
class AdminController(
   private val adminService: AdminService
) {

    @GetMapping("/contests")
    fun getRunningAndUpcomingContests(): HttpEntity<List<ContestDto>> {
        val contests = adminService.getRunningAndUpcomingContests()

        return ResponseEntity.ok(contests)
    }
}