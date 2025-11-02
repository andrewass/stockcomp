package com.stockcomp.configuration

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@Target(AnnotationTarget.CLASS)
@Import(TestcontainersConfiguration::class)
annotation class ControllerIntegrationTest
