package com.stockcomp.controller

import com.stockcomp.IntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import javax.transaction.Transactional

@Transactional
@AutoConfigureMockMvc
internal class SchedulerControllerIT : IntegrationTest() {

    @Test
    fun `should create contest`(){
        assertEquals(2,2)
    }

}