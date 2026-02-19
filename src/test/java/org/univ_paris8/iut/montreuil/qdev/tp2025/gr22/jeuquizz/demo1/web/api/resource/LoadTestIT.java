package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.resource;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.persistence.EntityManagerUtil;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.filter.AuthenticationFilter;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.mapper.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de charge simples.
 * Vérifie que l'API supporte des requêtes concurrentes sans erreur.
 */
class LoadTestIT extends JerseyTest {

    @Override
    protected Application configure() {
        EntityManagerUtil.init("testPU");
        return new ResourceConfig()
                .register(AnnonceResource.class)
                .register(AuthenticationFilter.class)
                .register(EntityNotFoundExceptionMapper.class)
                .register(BusinessExceptionMapper.class)
                .register(ConflictExceptionMapper.class)
                .register(GenericExceptionMapper.class);
    }

    @AfterAll
    static void cleanup() {
        EntityManagerUtil.close();
    }

    @Test
    @DisplayName("Charge : 50 requêtes GET /annonces concurrentes")
    void testConcurrentGetRequests() throws Exception {
        int nbRequests = 50;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<Integer>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < nbRequests; i++) {
            futures.add(executor.submit(() -> {
                Response response = target("/annonces")
                        .queryParam("page", 0)
                        .queryParam("size", 5)
                        .request().get();
                return response.getStatus();
            }));
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS));

        long duration = System.currentTimeMillis() - startTime;

        // Vérifier que toutes les requêtes ont réussi
        int successCount = 0;
        for (Future<Integer> future : futures) {
            int status = future.get();
            if (status == 200) successCount++;
        }

        System.out.println("=== RÉSULTATS TEST DE CHARGE ===");
        System.out.println("Requêtes totales  : " + nbRequests);
        System.out.println("Requêtes réussies : " + successCount);
        System.out.println("Durée totale      : " + duration + " ms");
        System.out.println("Débit moyen       : " + (nbRequests * 1000 / Math.max(duration, 1)) + " req/s");

        // Au moins 90% de succès
        assertTrue(successCount >= nbRequests * 0.9,
                "Au moins 90% des requêtes doivent réussir, obtenu : " + successCount + "/" + nbRequests);
    }
}
