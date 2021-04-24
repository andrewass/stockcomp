package com.stockcomp.controller;

import com.stockcomp.controller.common.CookieUtilKt;
import com.stockcomp.controller.common.CustomExceptionHandler;
import com.stockcomp.entity.contest.Contest;
import com.stockcomp.entity.contest.Transaction;
import com.stockcomp.request.InvestmentTransactionRequest;
import com.stockcomp.service.ContestService;
import com.stockcomp.service.InvestmentService;
import com.stockcomp.util.JwtUtilKt;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @GetMapping("/user-participating")
    @ApiOperation(value = "Verify if user is participating in a given contest")
    public ResponseEntity<Boolean> isParticipating(HttpServletRequest request, @RequestParam Integer contesteNumber) {
        var jwt = CookieUtilKt.getJwtFromCookie(request);
        var username = JwtUtilKt.extractUsername(jwt);

        return ResponseEntity.ok(true);
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
