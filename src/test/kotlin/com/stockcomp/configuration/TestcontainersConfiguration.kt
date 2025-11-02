package com.stockcomp.configuration

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration
class TestcontainersConfiguration {
    @Bean
    @ServiceConnection
    fun postgresContainer() = postgresContainer

    companion object {
        private val postgresContainer = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:16.3-bullseye"))
    }
}
