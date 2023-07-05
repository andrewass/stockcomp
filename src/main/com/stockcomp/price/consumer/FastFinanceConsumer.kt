package com.stockcomp.price.consumer

import com.stockcomp.contest.dto.RealTimePrice
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI

@Component("fastfinance.quote.consumer")
class FastFinanceConsumer(
    private val webClient: WebClient
) : QuoteConsumer {

    @Value("\${fastfinance.base.url}")
    private lateinit var baseUrl: String

    override fun getRealTimePrice(symbol: String): RealTimePrice {
        return webClient.get()
            .uri(URI("$baseUrl/get-random-number"))
            .retrieve()
            .bodyToMono(RealTimePrice::class.java)
            .block()!!
    }
}