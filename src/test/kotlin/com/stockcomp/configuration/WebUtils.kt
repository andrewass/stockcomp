package com.stockcomp.configuration

import com.stockcomp.user.AccountStatusAuthority
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
    emailClaim: String? = null,
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

fun mockMvcPutRequest(
    url: String,
    role: String = "USER",
    emailClaim: String? = null,
) = MockMvcRequestBuilders
    .put(url)
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
        postProcessor.jwt { jwt ->
            jwt
                .claim("sub", "google-subject-$emailClaim")
                .claim("email", emailClaim)
                .claim("email_verified", true)
        }
    }
    val authorities = mutableListOf(SimpleGrantedAuthority(AccountStatusAuthority.ACTIVE))
    if (role == "ADMIN") authorities += SimpleGrantedAuthority("ROLE_ADMIN")
    postProcessor.authorities(*authorities.toTypedArray())
    return postProcessor
}
