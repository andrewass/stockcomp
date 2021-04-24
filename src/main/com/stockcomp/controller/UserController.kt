package com.stockcomp.controller

import org.springframework.web.bind.annotation.RequestMapping
import com.stockcomp.service.CustomUserService
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(private val userService: CustomUserService)