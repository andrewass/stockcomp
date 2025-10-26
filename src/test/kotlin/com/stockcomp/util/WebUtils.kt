package com.stockcomp.util

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

fun mockMvcGetRequest(url: String) =
    MockMvcRequestBuilders
        .get(url)
        .with(SecurityMockMvcRequestPostProcessors.jwt())
