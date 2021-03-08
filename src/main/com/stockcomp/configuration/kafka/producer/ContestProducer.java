package com.stockcomp.configuration.kafka.producer;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ContestProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NewTopic signUpTopic;
    private final NewTopic contestPurchaseTopic;

    public ContestProducer(KafkaTemplate<String, String> kafkaTemplate, NewTopic signUpTopic,
                           NewTopic contestPurchaseTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.signUpTopic = signUpTopic;
        this.contestPurchaseTopic = contestPurchaseTopic;
    }

    public void sendSignUp(String input){

    }

}
