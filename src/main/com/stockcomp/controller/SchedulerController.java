package com.stockcomp.controller;

import com.stockcomp.entity.contest.Contest;
import com.stockcomp.request.CreateContestRequest;
import com.stockcomp.service.ContestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

    private final ContestService contestService;

    public SchedulerController(ContestService contestService) {
        this.contestService = contestService;
    }

    @PostMapping("/create-contest")
    public ResponseEntity<Contest> createContest(@RequestBody CreateContestRequest request){
        var contest = contestService.createContest(request);

        return ResponseEntity.ok(contest);
    }

    @PostMapping("/start-contest")
    public ResponseEntity<HttpStatus> startContest(@RequestParam("contestNumber") Integer contestNumber) {
        contestService.startContest(contestNumber);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/stop-contest")
    public ResponseEntity<HttpStatus> stopContest(@RequestParam("contestNumber") Integer contestNumber) {
        contestService.stopContest(contestNumber);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
