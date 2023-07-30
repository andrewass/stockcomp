package com.stockcomp.price.consumer

import com.stockcomp.contest.dto.CurrentPriceSymbol
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

    override fun getCurrentPrice(symbol: String): CurrentPriceSymbol {
        return webClient.get()
            .uri(URI("$baseUrl/price/current-price-symbol/$symbol"))
            .retrieve()
            .bodyToMono(CurrentPriceSymbol::class.java)
            .block()!!
    }
}