package com.stockcomp.contest.consumer

import com.stockcomp.contest.dto.RealTimePriceDto
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder

@Component
class DefaultQuoteConsumer(
    private val webClient: WebClient
) : QuoteConsumer {

    override fun getRealTimePrice(symbol: String): RealTimePriceDto {
        return webClient.get()
            .uri { uriBuilder: UriBuilder ->
                uriBuilder.path("/stock/stock-quote/$symbol").build()
            }
            .retrieve()
            .bodyToMono(RealTimePriceDto::class.java)
            .block()!!
    }
}