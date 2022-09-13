package com.stockcomp.authentication.consumer

import com.stockcomp.authentication.dto.TokenValidationResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.net.URI


@Component
class DefaultAuthorizationConsumer(
    private val webClient: WebClient
) : AuthorizationConsumer {

    @Value("\${authorization.base.url}")
    private lateinit var authorizationUrl: String

    override fun validateAccessToken(token: String): TokenValidationResponse {
        val bodyValues: MultiValueMap<String, String> = LinkedMultiValueMap()
        bodyValues.add("token", token)
        bodyValues.add("token_type_hint", "access_token")

        return webClient.post()
            .uri(URI("$authorizationUrl/token/introspect"))
            .body(BodyInserters.fromFormData(bodyValues))
            .retrieve()
            .bodyToMono(TokenValidationResponse::class.java)
            .block()!!
    }
}