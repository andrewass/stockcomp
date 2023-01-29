package com.stockcomp.contest.consumer

import com.stockcomp.contest.dto.RealTimePrice
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI

@Component
class DefaultQuoteConsumer(
    private val webClient: WebClient
) : QuoteConsumer {

    @Value("\${consumer.base.url}")
    private lateinit var consumerUrl: String

    override fun getRealTimePrice(symbol: String): RealTimePrice {
        return webClient.get()
            .uri(URI("$consumerUrl/stock/stock-quote?symbol=$symbol"))
            .retrieve()
            .bodyToMono(RealTimePrice::class.java)
            .block()!!
    }
}