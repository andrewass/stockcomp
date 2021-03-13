package com.stockcomp.configuration.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Value(value = "${kafka.partitions}")
    private Integer partitions;

    @Value(value = "${kafka.replication.factor}")
    private Short replicationFactor;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);

        return new KafkaAdmin(configs);
    }

    @Bean(name = "sell-investment")
    public NewTopic signUpTopic() {

        return new NewTopic("sell-investment", partitions, replicationFactor);
    }

    @Bean(name = "buy-investment")
    public NewTopic contestPurchaseTopic() {

        return new NewTopic("buy-investment", partitions, replicationFactor);
    }
}