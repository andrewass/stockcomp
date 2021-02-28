package com.stockcomp.controller;

import com.stockcomp.tasks.ContestScheduler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

    private final ContestScheduler contestScheduler;

    public SchedulerController(ContestScheduler contestScheduler) {
        this.contestScheduler = contestScheduler;
    }

    @PostMapping("/start-contest")
    public ResponseEntity<HttpStatus> startContest() {
        contestScheduler.startContest();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/stop-contest")
    public ResponseEntity<HttpStatus> stopContest() {
        contestScheduler.stopContest();

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
