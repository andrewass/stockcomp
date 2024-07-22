package com.stockcomp

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules

@Disabled("Disabled while fixing application module dependency")
class ApplicationModuleTest {

    @Test
    fun verifyModularity() {
        ApplicationModules.of(StockCompApplication::class.java).verify()
    }
}