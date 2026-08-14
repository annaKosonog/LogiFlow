package com.io.logiflow.packageConsumer.infrastructure.controllers.integrations;

import com.io.logiflow.packageConsumer.domain.repository.PackageRepository;
import com.io.logiflow.packageConsumer.domain.repository.TruckRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("testdb")
                    .withUsername("postgres")
                    .withPassword("postgres");

    static {
        postgres.start();
    }

    @Autowired
    private PackageRepository packageRepository;
    @Autowired
    private TruckRepository truckRepository;

    @AfterEach
    void cleanUpDatabase() {
        try {
            packageRepository.deleteAll();
            packageRepository.flush();
            truckRepository.deleteAll();
            truckRepository.flush();
        } catch (Exception e) {
            throw e;
        }
    }
}
