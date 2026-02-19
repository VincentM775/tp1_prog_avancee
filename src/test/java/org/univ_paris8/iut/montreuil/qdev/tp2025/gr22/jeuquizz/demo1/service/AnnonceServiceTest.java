package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service;

import org.junit.jupiter.api.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.User;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour AnnonceService.
 *
 * Ces tests vérifient :
 * - Les opérations CRUD
 * - Les règles métier
 * - La gestion des transactions
 * - La pagination
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnnonceServiceTest {

    private static AnnonceService annonceService;
    private static UserService userService;
    private static CategoryService categoryService;

    // Données de test
    private static User testUser;
    private static Category testCategory;
    private static Annonce testAnnonce;

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @BeforeAll
    static void setUp() {
        annonceService = new AnnonceService();
        userService = new UserService();
        categoryService = new CategoryService();
    }

    // ==================== PRÉPARATION DES DONNÉES ====================

    @Test
    @Order(1)
    @DisplayName("Préparer les données de test (User et Category)")
    void prepareTestData() {
        String suffix = uniqueSuffix();

        // Créer un utilisateur de test
        testUser = userService.creer(
            "serviceuser_" + suffix,
            "serviceuser_" + suffix + "@test.com",
            "password123"
        );
        assertNotNull(testUser.getId());

        // Créer une catégorie de test
        testCategory = categoryService.creer("ServiceCategorie_" + suffix);
        assertNotNull(testCategory.getId());
    }

    // ==================== TEST CRÉATION ====================

    @Test
    @Order(10)
    @DisplayName("creer() - Création d'une annonce avec succès")
    void testCreerAnnonce() {
        Annonce annonce = new Annonce(
            "Test Service",
            "Description de test",
            "Adresse test",
            "test@service.com"
        );

        testAnnonce = annonceService.creer(annonce, testUser.getId(), testCategory.getId());

        assertNotNull(testAnnonce.getId(), "L'ID doit être généré");
        assertEquals(AnnonceStatus.DRAFT, testAnnonce.getStatus(), "Le statut initial doit être DRAFT");
        assertNotNull(testAnnonce.getDate(), "La date doit être définie");
        assertEquals(testUser.getId(), testAnnonce.getAuthor().getId());
        assertEquals(testCategory.getId(), testAnnonce.getCategory().getId());
    }

    @Test
    @Order(11)
    @DisplayName("creer() - Échec si l'auteur n'existe pas")
    void testCreerAnnonceAuteurInexistant() {
        Annonce annonce = new Annonce("Test", "Desc", "Addr", "mail@test.com");

        assertThrows(EntityNotFoundException.class, () -> {
            annonceService.creer(annonce, 999999L, testCategory.getId());
        });
    }

    @Test
    @Order(12)
    @DisplayName("creer() - Échec si la catégorie n'existe pas")
    void testCreerAnnonceCategorieInexistante() {
        Annonce annonce = new Annonce("Test", "Desc", "Addr", "mail@test.com");

        assertThrows(EntityNotFoundException.class, () -> {
            annonceService.creer(annonce, testUser.getId(), 999999L);
        });
    }

    // ==================== TEST MODIFICATION ====================

    @Test
    @Order(20)
    @DisplayName("modifier() - Modification avec succès")
    void testModifierAnnonce() {
        Annonce modifiee = annonceService.modifier(
            testAnnonce.getId(),
            "Titre modifié",
            "Nouvelle description",
            null,  // adresse inchangée
            null,  // mail inchangé
            null,  // catégorie inchangée
            testUser.getId()
        );

        assertEquals("Titre modifié", modifiee.getTitle());
        assertEquals("Nouvelle description", modifiee.getDescription());
    }

    // ==================== TEST PUBLICATION ====================

    @Test
    @Order(30)
    @DisplayName("publier() - Publication avec succès")
    void testPublierAnnonce() {
        Annonce publiee = annonceService.publier(testAnnonce.getId(), testUser.getId());

        assertEquals(AnnonceStatus.PUBLISHED, publiee.getStatus());
    }

    @Test
    @Order(31)
    @DisplayName("publier() - Échec si déjà publiée")
    void testPublierAnnonceDejaPubliee() {
        // L'annonce est déjà publiée (test précédent)
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            annonceService.publier(testAnnonce.getId(), testUser.getId());
        });

        assertTrue(exception.getMessage().contains("déjà publiée"));
    }

    // ==================== TEST ARCHIVAGE ====================

    @Test
    @Order(40)
    @DisplayName("archiver() - Archivage avec succès")
    void testArchiverAnnonce() {
        Annonce archivee = annonceService.archiver(testAnnonce.getId(), testUser.getId());

        assertEquals(AnnonceStatus.ARCHIVED, archivee.getStatus());
    }

    @Test
    @Order(41)
    @DisplayName("archiver() - Échec si déjà archivée")
    void testArchiverAnnonceDejaArchivee() {
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            annonceService.archiver(testAnnonce.getId(), testUser.getId());
        });

        assertTrue(exception.getMessage().contains("déjà archivée"));
    }

    @Test
    @Order(42)
    @DisplayName("modifier() - Échec si annonce archivée")
    void testModifierAnnonceArchivee() {
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            annonceService.modifier(testAnnonce.getId(), "Nouveau titre", null, null, null, null, testUser.getId());
        });

        assertTrue(exception.getMessage().contains("archivée"));
    }

    @Test
    @Order(43)
    @DisplayName("publier() - Échec si annonce archivée")
    void testPublierAnnonceArchivee() {
        ConflictException exception = assertThrows(ConflictException.class, () -> {
            annonceService.publier(testAnnonce.getId(), testUser.getId());
        });

        assertTrue(exception.getMessage().contains("archivée"));
    }

    // ==================== TEST RECHERCHE ET PAGINATION ====================

    @Test
    @Order(50)
    @DisplayName("listerPubliees() - Pagination")
    void testListerPubliees() {
        // Créer plusieurs annonces publiées
        String suffix = uniqueSuffix();
        for (int i = 0; i < 5; i++) {
            Annonce a = new Annonce("Pagination " + i + "_" + suffix, "Desc", "Addr", "mail@test.com");
            Annonce created = annonceService.creer(a, testUser.getId(), null);
            annonceService.publier(created.getId(), testUser.getId());
        }

        // Tester la pagination
        PagedResult<Annonce> page0 = annonceService.listerPubliees(0, 2);

        assertEquals(2, page0.getContent().size(), "Page 0 doit avoir 2 éléments");
        assertTrue(page0.getTotalElements() >= 5, "Au moins 5 éléments au total");
        assertTrue(page0.hasNext(), "Il doit y avoir une page suivante");
    }

    @Test
    @Order(51)
    @DisplayName("rechercher() - Recherche par mot-clé")
    void testRechercherParMotCle() {
        PagedResult<Annonce> results = annonceService.rechercher(
            "Pagination",  // mot-clé
            null,          // pas de filtre catégorie
            AnnonceStatus.PUBLISHED,
            0, 10
        );

        assertFalse(results.getContent().isEmpty());
        for (Annonce a : results.getContent()) {
            assertTrue(
                a.getTitle().toLowerCase().contains("pagination") ||
                a.getDescription().toLowerCase().contains("pagination")
            );
        }
    }

    @Test
    @Order(52)
    @DisplayName("rechercher() - Filtrage par catégorie et statut")
    void testRechercherAvecFiltres() {
        // Créer une annonce avec catégorie
        String suffix = uniqueSuffix();
        Annonce a = new Annonce("Filtrage test " + suffix, "Desc", "Addr", "mail@test.com");
        Annonce created = annonceService.creer(a, testUser.getId(), testCategory.getId());
        annonceService.publier(created.getId(), testUser.getId());

        // Rechercher avec filtres
        PagedResult<Annonce> results = annonceService.rechercher(
            null,
            testCategory.getId(),
            AnnonceStatus.PUBLISHED,
            0, 10
        );

        assertFalse(results.getContent().isEmpty());
        for (Annonce annonce : results.getContent()) {
            assertEquals(testCategory.getId(), annonce.getCategory().getId());
            assertEquals(AnnonceStatus.PUBLISHED, annonce.getStatus());
        }
    }

    // ==================== TEST SUPPRESSION ====================

    @Test
    @Order(100)
    @DisplayName("supprimer() - Suppression avec succès")
    void testSupprimerAnnonce() {
        // Créer une annonce pour la supprimer
        String suffix = uniqueSuffix();
        Annonce a = new Annonce("A supprimer " + suffix, "Desc", "Addr", "mail@test.com");
        Annonce created = annonceService.creer(a, testUser.getId(), null);
        Long id = created.getId();

        // Archiver avant de supprimer (règle métier)
        annonceService.publier(id, testUser.getId());
        annonceService.archiver(id, testUser.getId());

        // Supprimer
        annonceService.supprimer(id, testUser.getId());

        // Vérifier
        Optional<Annonce> deleted = annonceService.trouverParId(id);
        assertFalse(deleted.isPresent(), "L'annonce doit être supprimée");
    }

    @Test
    @Order(101)
    @DisplayName("supprimer() - Échec si annonce inexistante")
    void testSupprimerAnnonceInexistante() {
        assertThrows(EntityNotFoundException.class, () -> {
            annonceService.supprimer(999999L, testUser.getId());
        });
    }
}
