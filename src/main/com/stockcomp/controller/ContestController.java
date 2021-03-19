package com.stockcomp.controller;

import com.stockcomp.entity.contest.Contest;
import com.stockcomp.kafka.producer.ContestProducer;
import com.stockcomp.request.InvestmentTransactionRequest;
import com.stockcomp.service.ContestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contest")
public class ContestController extends CustomExceptionHandler {

    private final ContestService contestService;
    private final ContestProducer contestProducer;

    public ContestController(ContestProducer contestProducer, ContestService contestService) {
        this.contestProducer = contestProducer;
        this.contestService = contestService;
    }

    @GetMapping("/upcoming-contests")
    public ResponseEntity<List<Contest>> getUpcomingContests(){
        var contests = contestService.getUpcomingContests();

        return ResponseEntity.ok(contests);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<HttpStatus> signUpForContest(
            @RequestParam("username") String username,
            @RequestParam("contestNumber") Integer contestNumber
    ) {
        contestService.signUpUser(username, contestNumber);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/buy-investment")
    public ResponseEntity<HttpStatus> buyInvestment(@RequestBody InvestmentTransactionRequest request) {
        contestProducer.buyInvestment(request);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/sell-investment")
    public ResponseEntity<HttpStatus> sellInvestment(@RequestBody InvestmentTransactionRequest request) {
        contestProducer.sellInvestment(request);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
