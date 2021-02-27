package com.stockcomp.controller;

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

    @Value("${kafka.contest.sign.up.topic}")
    private String contestSignUpTopic;

    public ContestController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<HttpStatus> signUpForContest(@RequestParam("username") String username) {
        kafkaTemplate.send(contestSignUpTopic, "Signing up user" + username);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
