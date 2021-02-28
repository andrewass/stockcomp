package com.stockcomp.configuration.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ContestConsumer {

    private final Logger logger = LoggerFactory.getLogger(ContestConsumer.class);

    @KafkaListener(id = "contest-id", topics = "contest-sign-up", groupId = "groupId", autoStartup = "false")
    public void consumeMessage(String message) {
        logger.info("Consumed message : " + message);
    }
}