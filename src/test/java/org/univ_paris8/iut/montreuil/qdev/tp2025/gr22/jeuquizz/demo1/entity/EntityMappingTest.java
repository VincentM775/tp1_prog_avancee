package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.persistence.EntityManagerUtil;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour vérifier le mapping JPA des entités.
 *
 * Ces tests vérifient que :
 * - Les tables sont correctement générées par Hibernate
 * - Les entités peuvent être persistées et récupérées
 * - Les relations fonctionnent correctement
 *
 * IMPORTANT : Les tests utilisent des UUID pour garantir l'unicité des données
 * et permettre la ré-exécution des tests sans conflit.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EntityMappingTest {

    private EntityManager em;

    // Génère un suffixe unique pour chaque exécution de test
    private String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @BeforeEach
    void setUp() {
        em = EntityManagerUtil.getEntityManager();
    }

    @AfterEach
    void tearDown() {
        if (em != null && em.isOpen()) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
        }
    }

    // NE PAS fermer l'EntityManagerFactory ici !
    // Cela empêcherait les autres classes de test de fonctionner.
    // La fermeture sera gérée par la JVM à la fin de tous les tests.

    @Test
    @Order(1)
    @DisplayName("Test création et persistance d'un User")
    void testPersistUser() {
        String suffix = uniqueSuffix();
        em.getTransaction().begin();

        User user = new User("testuser_" + suffix, "test_" + suffix + "@example.com", "password123");
        em.persist(user);
        em.flush();

        assertNotNull(user.getId(), "L'ID devrait être généré après persist");
        assertNotNull(user.getCreatedAt(), "createdAt devrait être initialisé");

        em.getTransaction().commit();

        // Vérifier que l'utilisateur peut être récupéré
        User found = em.find(User.class, user.getId());
        assertEquals("testuser_" + suffix, found.getUsername());
    }

    @Test
    @Order(2)
    @DisplayName("Test création et persistance d'une Category")
    void testPersistCategory() {
        String suffix = uniqueSuffix();
        em.getTransaction().begin();

        Category category = new Category("Immobilier_" + suffix);
        em.persist(category);
        em.flush();

        assertNotNull(category.getId(), "L'ID devrait être généré après persist");

        em.getTransaction().commit();

        Category found = em.find(Category.class, category.getId());
        assertEquals("Immobilier_" + suffix, found.getLabel());
    }

    @Test
    @Order(3)
    @DisplayName("Test création d'une Annonce avec relations")
    void testPersistAnnonceWithRelations() {
        String suffix = uniqueSuffix();
        em.getTransaction().begin();

        // Créer un utilisateur
        User author = new User("annonceur_" + suffix, "annonceur_" + suffix + "@test.com", "secret123");
        em.persist(author);

        // Créer une catégorie
        Category category = new Category("Véhicules_" + suffix);
        em.persist(category);

        // Créer une annonce liée à l'utilisateur et à la catégorie
        Annonce annonce = new Annonce("Voiture à vendre", "Belle voiture en bon état", "Paris 75001", "contact@test.com");
        annonce.setAuthor(author);
        annonce.setCategory(category);
        annonce.setStatus(AnnonceStatus.PUBLISHED);
        em.persist(annonce);

        em.flush();
        em.getTransaction().commit();

        // Vérifier les données
        assertNotNull(annonce.getId());
        assertEquals(AnnonceStatus.PUBLISHED, annonce.getStatus());
        assertNotNull(annonce.getDate());

        // Récupérer et vérifier les relations
        em.clear(); // Vider le cache pour forcer un rechargement depuis la BDD
        Annonce found = em.find(Annonce.class, annonce.getId());

        assertNotNull(found.getAuthor(), "L'auteur devrait être chargé");
        assertEquals("annonceur_" + suffix, found.getAuthor().getUsername());

        assertNotNull(found.getCategory(), "La catégorie devrait être chargée");
        assertEquals("Véhicules_" + suffix, found.getCategory().getLabel());
    }

    @Test
    @Order(4)
    @DisplayName("Test relation bidirectionnelle User -> Annonces")
    void testUserAnnoncesRelation() {
        String suffix = uniqueSuffix();
        em.getTransaction().begin();

        User user = new User("multiannonce_" + suffix, "multi_" + suffix + "@test.com", "pass456");
        Category cat = new Category("Services_" + suffix);
        em.persist(user);
        em.persist(cat);

        // Ajouter plusieurs annonces via la méthode utilitaire
        Annonce a1 = new Annonce("Service 1", "Description 1", "Adresse 1", "s1@test.com");
        Annonce a2 = new Annonce("Service 2", "Description 2", "Adresse 2", "s2@test.com");

        a1.setCategory(cat);
        a2.setCategory(cat);

        user.addAnnonce(a1);
        user.addAnnonce(a2);

        em.persist(a1);
        em.persist(a2);

        em.flush();
        em.getTransaction().commit();

        // Vérifier que l'utilisateur a bien 2 annonces
        em.clear();
        User found = em.find(User.class, user.getId());
        assertEquals(2, found.getAnnonces().size(), "L'utilisateur devrait avoir 2 annonces");
    }

    @Test
    @Order(5)
    @DisplayName("Test enum AnnonceStatus stocké en String")
    void testEnumStatusAsString() {
        em.getTransaction().begin();

        Annonce annonce = new Annonce("Test Enum", "Description", "Adresse", "enum@test.com");
        annonce.setStatus(AnnonceStatus.ARCHIVED);
        em.persist(annonce);

        em.flush();
        em.getTransaction().commit();

        // Vérifier via requête native que le status est bien stocké en texte
        Object result = em.createNativeQuery(
            "SELECT status FROM annonces WHERE id = :id")
            .setParameter("id", annonce.getId())
            .getSingleResult();

        assertEquals("ARCHIVED", result, "Le statut devrait être stocké comme 'ARCHIVED' en texte");
    }
}
