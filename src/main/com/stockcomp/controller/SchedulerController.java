package com.stockcomp.controller;

import com.stockcomp.request.CreateContestRequest;
import com.stockcomp.service.ContestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

    private final ContestService contestService;

    public SchedulerController(ContestService contestService) {
        this.contestService = contestService;
    }

    @PostMapping("/create-contest")
    public ResponseEntity<HttpStatus> createContest(@RequestBody CreateContestRequest request){
        contestService.createContest(request);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/start-contest")
    public ResponseEntity<HttpStatus> startContest() {
        contestService.startContest();

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/stop-contest")
    public ResponseEntity<HttpStatus> stopContest() {
        contestService.stopContest();

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
