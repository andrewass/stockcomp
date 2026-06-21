package com.stockcomp.common

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CompetitionRankingTest {
    @Test
    fun `should assign shared competition ranks and skip following positions`() {
        val ranks =
            competitionRanksForSortedValues(
                listOf(
                    BigDecimal("25000.00"),
                    BigDecimal("25000.00"),
                    BigDecimal("23000.00"),
                    BigDecimal("21000.00"),
                ),
            )

        assertEquals(listOf(1, 1, 3, 4), ranks)
    }

    @Test
    fun `should assign shared overall rank for equal scores`() {
        val ranks = competitionRanksForSortedValues(listOf(6, 6, 3, 0))

        assertEquals(listOf(1, 1, 3, 4), ranks)
    }
}
