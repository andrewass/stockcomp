package com.stockcomp.controller;

import com.stockcomp.service.CustomUserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final CustomUserService userService;

    public UserController(CustomUserService userService) {
        this.userService = userService;
    }
}
