package com.stockcomp.controller;

import com.stockcomp.service.ContestService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contest")
public class ContestController {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ContestService contestService;

    @Value("${kafka.contest.sign.up.topic}")
    private String contestSignUpTopic;

    public ContestController(KafkaTemplate<String, String> kafkaTemplate, ContestService contestService) {
        this.kafkaTemplate = kafkaTemplate;
        this.contestService = contestService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<HttpStatus> signUpForContest(
            @RequestParam("username") String username,
            @RequestParam("contestNumber") Integer contestNumber
    ) {
        contestService.signUpUser(username, contestNumber);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
