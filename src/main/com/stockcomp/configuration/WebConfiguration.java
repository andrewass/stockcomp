package com.stockcomp.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfiguration {

    @Value("${finnhub.base.url}")
    private String finnhubBaseUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(finnhubBaseUrl).build();
    }
}
