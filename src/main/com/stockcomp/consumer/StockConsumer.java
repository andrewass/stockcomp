package com.stockcomp.consumer;

import com.stockcomp.response.RealTimePriceResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class StockConsumer {

    private final String finnhubToken = System.getenv("FINNHUB_API_KEY");

    private final WebClient webClient;

    public StockConsumer(WebClient webClient) {
        this.webClient = webClient;
    }

    public RealTimePriceResponse findRealTimePrice(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/quote")
                        .queryParam("symbol", symbol)
                        .queryParam("token", finnhubToken).build())
                .retrieve()
                .bodyToMono(RealTimePriceResponse.class)
                .block();
    }
}
