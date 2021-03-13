package com.stockcomp.configuration.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ContestConsumer {

    private final Logger logger = LoggerFactory.getLogger(ContestConsumer.class);

    @KafkaListener(topics = "buy-investment", groupId = "groupId")
    public void consumeBuyInvestment(String message) {
        logger.info("Consumed buy-investment : " + message);
    }

    @KafkaListener(topics = "sell-investment", groupId = "groupId")
    public void consumeSellInvestment(String message){
        logger.info("Consumed sell-investment : "+ message);
    }
}