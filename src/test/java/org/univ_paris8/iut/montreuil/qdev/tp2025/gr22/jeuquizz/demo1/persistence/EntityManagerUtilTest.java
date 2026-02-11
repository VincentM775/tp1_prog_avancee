package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pour vérifier que JPA/Hibernate est correctement configuré.
 *
 * Prérequis : PostgreSQL doit être démarré et la base de données "masterannonce" doit exister.
 *
 * NOTE : On ne ferme PAS l'EntityManagerFactory dans @AfterAll car cela empêcherait
 * les autres classes de test de fonctionner (l'ordre d'exécution n'est pas garanti).
 */
class EntityManagerUtilTest {

    @Test
    void testEntityManagerFactoryNotNull() {
        EntityManagerFactory emf = EntityManagerUtil.getEntityManagerFactory();
        assertNotNull(emf, "L'EntityManagerFactory ne devrait pas être null");
        assertTrue(emf.isOpen(), "L'EntityManagerFactory devrait être ouverte");
    }

    @Test
    void testCreateEntityManager() {
        EntityManager em = EntityManagerUtil.getEntityManager();
        assertNotNull(em, "L'EntityManager ne devrait pas être null");
        assertTrue(em.isOpen(), "L'EntityManager devrait être ouvert");
        em.close();
    }

    @Test
    void testDatabaseConnection() {
        EntityManager em = EntityManagerUtil.getEntityManager();
        try {
            // Exécute une requête native simple pour vérifier la connexion
            Object result = em.createNativeQuery("SELECT 1").getSingleResult();
            assertNotNull(result, "La requête devrait retourner un résultat");
            assertEquals(1, ((Number) result).intValue(), "Le résultat devrait être 1");
        } finally {
            em.close();
        }
    }

    // NE PAS fermer l'EntityManagerFactory ici !
    // La JVM s'en chargera à la fin de tous les tests.
}
