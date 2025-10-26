package com.stockcomp.modularity

import com.stockcomp.StockCompApplication
import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules

class ApplicationModuleTest {
    @Test
    fun verifyModularity() {
        ApplicationModules.of(StockCompApplication::class.java).verify()
    }
}
