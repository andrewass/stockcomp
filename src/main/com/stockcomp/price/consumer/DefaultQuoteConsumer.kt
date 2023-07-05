package com.stockcomp.price.consumer

import com.stockcomp.contest.dto.RealTimePrice
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI

@Component("default.quote.consumer")
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