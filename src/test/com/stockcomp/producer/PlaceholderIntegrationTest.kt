package com.stockcomp.producer

import com.stockcomp.SpringBootTestConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@AutoConfigureMockMvc
internal class PlaceholderIntegrationTest : SpringBootTestConfig() {

    @Test
    fun `application context test`() {
        assertEquals(2, 2)
    }
}