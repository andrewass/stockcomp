package com.stockcomp.configuration

import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

fun mockMvcGetRequest(url: String, role: String = "USER") =
    MockMvcRequestBuilders
        .get(url)
        .with(getJwtRequestPostProcessor(role))
        .contentType(MediaType.APPLICATION_JSON)

fun mockMvcPostRequest(url: String, role: String = "USER") =
    MockMvcRequestBuilders
        .post(url)
        .with(getJwtRequestPostProcessor(role))
        .contentType(MediaType.APPLICATION_JSON)


private fun getJwtRequestPostProcessor(role: String): JwtRequestPostProcessor {
    return if (role == "USER") {
        SecurityMockMvcRequestPostProcessors.jwt()
    } else {
        SecurityMockMvcRequestPostProcessors.jwt()
            .authorities(SimpleGrantedAuthority("ROLE_ADMIN"))
    }
}
