package com.stockcomp.producer

import com.stockcomp.TestcontainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class PlaceholderIntegrationIT {

    @Test
    fun `application context test`() {
    }
}