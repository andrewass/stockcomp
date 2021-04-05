package com.stockcomp.controller;

import com.stockcomp.controller.common.CustomExceptionHandler;
import com.stockcomp.entity.contest.Contest;
import com.stockcomp.entity.contest.Transaction;
import com.stockcomp.request.InvestmentTransactionRequest;
import com.stockcomp.service.ContestService;
import com.stockcomp.service.InvestmentService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contest")
public class ContestController extends CustomExceptionHandler {

    private final ContestService contestService;

    private final InvestmentService investmentService;

    public ContestController(ContestService contestService, InvestmentService investmentService) {
        this.contestService = contestService;
        this.investmentService = investmentService;
    }

    @GetMapping("/upcoming-contests")
    @ApiOperation(value = "Return a list of upcoming contests")
    public ResponseEntity<List<Contest>> getUpcomingContests() {
        var contests = contestService.getUpcomingContests();

        return ResponseEntity.ok(contests);
    }

    @PostMapping("/sign-up")
    @ApiOperation(value = "Sing up user for a given contest")
    public ResponseEntity<HttpStatus> signUpForContest(
            @RequestParam String username,
            @RequestParam Integer contestNumber
    ) {
        contestService.signUpUser(username, contestNumber);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/buy-investment")
    @ApiOperation(value = "Buy investment for a given participant")
    public ResponseEntity<Transaction> buyInvestment(@RequestBody InvestmentTransactionRequest request) {
        var transaction = investmentService.buyInvestment(request);

        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/sell-investment")
    @ApiOperation(value = "Sell investment for a given participant")
    public ResponseEntity<Transaction> sellInvestment(@RequestBody InvestmentTransactionRequest request) {
        var transaction = investmentService.sellInvestment(request);

        return ResponseEntity.ok(transaction);
    }
}
