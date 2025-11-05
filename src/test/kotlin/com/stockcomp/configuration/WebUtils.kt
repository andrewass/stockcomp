package com.stockcomp.configuration

import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

fun mockMvcGetRequest(
    url: String,
    role: String = "USER",
    emailClaim: String? = null,
) = MockMvcRequestBuilders
    .get(url)
    .with(getJwtRequestPostProcessor(role, emailClaim))
    .contentType(MediaType.APPLICATION_JSON)

fun mockMvcPostRequest(
    url: String,
    role: String = "USER",
    emailClaim: String?? = null,
) = MockMvcRequestBuilders
    .post(url)
    .with(getJwtRequestPostProcessor(role, emailClaim))
    .contentType(MediaType.APPLICATION_JSON)

fun mockMvcPatchRequest(
    url: String,
    role: String = "USER",
    emailClaim: String? = null,
) = MockMvcRequestBuilders
    .patch(url)
    .with(getJwtRequestPostProcessor(role, emailClaim))
    .contentType(MediaType.APPLICATION_JSON)

fun mockMvcDeleteRequest(
    url: String,
    role: String = "USER",
    emailClaim: String? = null,
) = MockMvcRequestBuilders
    .delete(url)
    .with(getJwtRequestPostProcessor(role, emailClaim))
    .contentType(MediaType.APPLICATION_JSON)

private fun getJwtRequestPostProcessor(
    role: String,
    emailClaim: String?,
): JwtRequestPostProcessor {
    val postProcessor = SecurityMockMvcRequestPostProcessors.jwt()
    emailClaim?.also {
        postProcessor.jwt { jwt -> jwt.claim("email", emailClaim) }
    }
    if (role == "ADMIN") {
        postProcessor.authorities(SimpleGrantedAuthority("ROLE_ADMIN"))
    }
    return postProcessor
}
