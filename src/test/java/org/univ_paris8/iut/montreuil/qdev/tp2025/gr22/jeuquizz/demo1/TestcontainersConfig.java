package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Classe de base pour les tests d'intégration.
 * Utilise Testcontainers (PostgreSQL réel dans Docker) si Docker est disponible,
 * sinon retombe sur H2 en mémoire.
 */
public abstract class TestcontainersConfig {

    private static final Logger log = LoggerFactory.getLogger(TestcontainersConfig.class);
    private static final PostgreSQLContainer<?> postgres;
    private static final boolean DOCKER_AVAILABLE;

    static {
        PostgreSQLContainer<?> container = null;
        boolean available = false;
        try {
            container = new PostgreSQLContainer<>("postgres:16-alpine");
            container.start();
            available = true;
            log.info("Testcontainers PostgreSQL démarré : {}", container.getJdbcUrl());
        } catch (Exception e) {
            log.warn("Docker non disponible, fallback sur H2 : {}", e.getMessage());
        }
        postgres = container;
        DOCKER_AVAILABLE = available;
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        if (DOCKER_AVAILABLE && postgres != null) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.datasource.username", postgres::getUsername);
            registry.add("spring.datasource.password", postgres::getPassword);
            registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        } else {
            registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
            registry.add("spring.datasource.username", () -> "sa");
            registry.add("spring.datasource.password", () -> "");
            registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
            registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.H2Dialect");
        }
    }
}
