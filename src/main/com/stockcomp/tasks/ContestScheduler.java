package com.stockcomp.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.Lifecycle;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

@Component
public class ContestScheduler {

    private final Logger logger = LoggerFactory.getLogger(ContestScheduler.class);

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private boolean contestIsActive;

    public ContestScheduler(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry) {
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
    }

    public void startContest() {
        if(!contestIsActive) {
            logger.info("Starting contest");
            contestIsActive = true;
            new Thread(new Task()).start();
            startKafkaConsumersForContest();
        }
    }

    public void stopContest() {
        logger.info("Stopping contest");
        contestIsActive = false;
        stopKafkaConsumersForContest();
    }

    private void startKafkaConsumersForContest() {
        kafkaListenerEndpointRegistry.getAllListenerContainers()
                .forEach(Lifecycle::start);
    }

    private void stopKafkaConsumersForContest() {
        kafkaListenerEndpointRegistry.getAllListenerContainers()
                .forEach(Lifecycle::stop);
    }

    private class Task implements Runnable{

        @Override
        public void run() {
            while (contestIsActive){
                try {
                    Thread.sleep(10000);
                    logger.info("Running the contest");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
