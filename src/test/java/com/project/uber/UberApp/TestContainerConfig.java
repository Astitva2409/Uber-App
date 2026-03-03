package com.project.uber.UberApp;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class TestContainerConfig {

    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(
            DockerImageName.parse("postgis/postgis:15-3.3").asCompatibleSubstituteFor("postgres")
    )
            .withDatabaseName("uber_test_db")
            .withUsername("test")
            .withPassword("test");

    static {
        postgreSQLContainer.start();
    }
}