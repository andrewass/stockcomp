package com.stockcomp.configuration

import com.stockcomp.token.TokenArgumentResolver
import com.stockcomp.user.UserServiceExternal
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ClientCodecConfigurer
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfiguration(
    private val userService: UserServiceExternal,
) : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(TokenArgumentResolver(userService))
    }

    @Bean
    fun webClient(): WebClient =
        WebClient
            .builder()
            .codecs { configurer: ClientCodecConfigurer ->
                configurer
                    .defaultCodecs()
                    .maxInMemorySize(20 * 1024 * 1024)
            }.build()
}
