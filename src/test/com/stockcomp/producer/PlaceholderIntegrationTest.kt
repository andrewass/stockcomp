package com.stockcomp.producer

import com.stockcomp.IntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc

@AutoConfigureMockMvc
internal class PlaceholderIntegrationTest : IntegrationTest() {

    @Test
    fun `application context test`() {
        assertEquals(2, 2)
    }
}