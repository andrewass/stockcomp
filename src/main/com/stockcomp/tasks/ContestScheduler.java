package com.stockcomp.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ContestScheduler {

    private final Logger logger = LoggerFactory.getLogger(ContestScheduler.class);

    @Scheduled(fixedRate = 10000000)
    public void startContest() {
        logger.info("Starting contest");
    }
}
