package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EntityManagerUtil {

    private static final String DEFAULT_PU = "masterannoncePU";
    private static EntityManagerFactory entityManagerFactory;

    static {
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory(DEFAULT_PU);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de l'EntityManagerFactory : " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    private EntityManagerUtil() {
    }

    /**
     * Réinitialise l'EntityManagerFactory avec un persistence unit différent.
     * Utilisé par les tests pour basculer sur H2 In-Memory (testPU).
     */
    public static synchronized void init(String persistenceUnitName) {
        close();
        entityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public static void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
