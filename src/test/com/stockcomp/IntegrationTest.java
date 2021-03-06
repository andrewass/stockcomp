package com.stockcomp;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(properties = { "jwt.secret=testSecret","auto.start.tasks=false"})
public abstract class IntegrationTest {

    public static final MySQLContainer MY_SQL_CONTAINER;

    static {
        MY_SQL_CONTAINER = new MySQLContainer(DockerImageName.parse("mysql:8.0"))
                .withDatabaseName("integration-test-db")
                .withUsername("testuser")
                .withPassword("testpassword");
        MY_SQL_CONTAINER.start();
    }

    @DynamicPropertySource
    static void setMysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
    }
}
