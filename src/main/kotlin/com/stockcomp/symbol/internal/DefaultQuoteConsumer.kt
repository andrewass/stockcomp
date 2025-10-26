package com.stockcomp.symbol.internal

import com.stockcomp.symbol.CurrentPriceSymbolDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI

@Component("default.quote.consumer")
class DefaultQuoteConsumer(
    private val webClient: WebClient,
) : QuoteConsumer {
    @Value("\${consumer.base.url}")
    private lateinit var consumerUrl: String

    override fun getCurrentPrice(symbol: String): CurrentPriceSymbolDto =
        webClient
            .get()
            .uri(URI("$consumerUrl/stock/stock-quote?symbol=$symbol"))
            .retrieve()
            .bodyToMono(CurrentPriceSymbolDto::class.java)
            .block()!!
}
