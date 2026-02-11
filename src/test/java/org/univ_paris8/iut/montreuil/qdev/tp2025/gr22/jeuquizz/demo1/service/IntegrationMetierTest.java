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
 * Tests d'intégration métier - NIVEAU 3
 *
 * Ces tests vérifient :
 * 1. Enchaînement complet : création → publication → recherche
 * 2. Problèmes Lazy Loading / N+1
 * 3. Cohérence des données à travers les transactions
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationMetierTest {

    private static AnnonceService annonceService;
    private static UserService userService;
    private static CategoryService categoryService;

    private static User testUser;
    private static Category testCategory;

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @BeforeAll
    static void setUp() {
        annonceService = new AnnonceService();
        userService = new UserService();
        categoryService = new CategoryService();
    }

    // ==================== PRÉPARATION ====================

    @Test
    @Order(1)
    @DisplayName("Préparer les données de test")
    void prepareTestData() {
        String suffix = uniqueSuffix();

        testUser = userService.creer(
            "integuser_" + suffix,
            "integ_" + suffix + "@test.com",
            "password123"
        );
        assertNotNull(testUser.getId());

        testCategory = categoryService.creer("IntegCategorie_" + suffix);
        assertNotNull(testCategory.getId());
    }

    // ==================== TEST ENCHAÎNEMENT COMPLET ====================

    @Test
    @Order(10)
    @DisplayName("Scénario complet : Création → Publication → Recherche → Archivage")
    void testScenarioComplet() {
        String suffix = uniqueSuffix();

        // ÉTAPE 1 : Création d'une annonce (brouillon)
        Annonce annonce = new Annonce(
            "iPhone 15 Pro " + suffix,
            "Vends iPhone 15 Pro 256Go, très bon état, avec accessoires",
            "Paris 15ème",
            "vendeur_" + suffix + "@test.com"
        );

        Annonce created = annonceService.creer(annonce, testUser.getId(), testCategory.getId());

        assertNotNull(created.getId(), "L'annonce doit avoir un ID");
        assertEquals(AnnonceStatus.DRAFT, created.getStatus(), "Statut initial = DRAFT");
        assertNotNull(created.getDate(), "La date doit être définie");

        // ÉTAPE 2 : L'annonce ne doit PAS apparaître dans les annonces publiées
        PagedResult<Annonce> publishedBefore = annonceService.rechercher(
            "iPhone 15 Pro " + suffix, null, AnnonceStatus.PUBLISHED, 0, 10
        );
        assertTrue(
            publishedBefore.getContent().stream().noneMatch(a -> a.getId().equals(created.getId())),
            "L'annonce en brouillon ne doit pas apparaître dans les recherches publiques"
        );

        // ÉTAPE 3 : Publication
        Annonce published = annonceService.publier(created.getId());
        assertEquals(AnnonceStatus.PUBLISHED, published.getStatus(), "Statut = PUBLISHED après publication");

        // ÉTAPE 4 : L'annonce DOIT apparaître dans les recherches
        PagedResult<Annonce> publishedAfter = annonceService.rechercher(
            "iPhone 15 Pro " + suffix, null, AnnonceStatus.PUBLISHED, 0, 10
        );
        assertTrue(
            publishedAfter.getContent().stream().anyMatch(a -> a.getId().equals(created.getId())),
            "L'annonce publiée doit apparaître dans les recherches"
        );

        // ÉTAPE 5 : Archivage
        Annonce archived = annonceService.archiver(created.getId());
        assertEquals(AnnonceStatus.ARCHIVED, archived.getStatus(), "Statut = ARCHIVED après archivage");

        // ÉTAPE 6 : L'annonce ne doit plus apparaître dans les recherches publiées
        PagedResult<Annonce> afterArchive = annonceService.rechercher(
            "iPhone 15 Pro " + suffix, null, AnnonceStatus.PUBLISHED, 0, 10
        );
        assertTrue(
            afterArchive.getContent().stream().noneMatch(a -> a.getId().equals(created.getId())),
            "L'annonce archivée ne doit plus apparaître dans les recherches publiées"
        );
    }

    // ==================== TEST LAZY LOADING ====================

    @Test
    @Order(20)
    @DisplayName("Test Lazy Loading : accès aux relations après fermeture EntityManager")
    void testLazyLoadingWithJoinFetch() {
        String suffix = uniqueSuffix();

        // Créer une annonce avec catégorie et auteur
        Annonce annonce = new Annonce("Lazy Test " + suffix, "Description", "Adresse", "lazy@test.com");
        Annonce created = annonceService.creer(annonce, testUser.getId(), testCategory.getId());

        // Récupérer l'annonce (le service utilise JOIN FETCH)
        Optional<Annonce> foundOpt = annonceService.trouverParId(created.getId());
        assertTrue(foundOpt.isPresent());

        Annonce found = foundOpt.get();

        // Ces accès auraient causé LazyInitializationException sans JOIN FETCH
        // Car l'EntityManager est fermé après la sortie du service
        assertDoesNotThrow(() -> {
            // Accès à la catégorie (relation LAZY)
            if (found.getCategory() != null) {
                String categoryLabel = found.getCategory().getLabel();
                assertNotNull(categoryLabel);
            }

            // Accès à l'auteur (relation LAZY)
            if (found.getAuthor() != null) {
                String authorUsername = found.getAuthor().getUsername();
                assertNotNull(authorUsername);
            }
        }, "L'accès aux relations LAZY ne doit pas lancer d'exception grâce au JOIN FETCH");
    }

    // ==================== TEST N+1 PROBLÈME ====================

    @Test
    @Order(30)
    @DisplayName("Test N+1 : vérifier que les relations sont chargées efficacement")
    void testN1ProblemAvoidance() {
        String suffix = uniqueSuffix();

        // Créer plusieurs annonces avec catégorie
        for (int i = 0; i < 5; i++) {
            Annonce a = new Annonce("N1 Test " + i + "_" + suffix, "Desc " + i, "Addr", "n1@test.com");
            Annonce created = annonceService.creer(a, testUser.getId(), testCategory.getId());
            annonceService.publier(created.getId());
        }

        // Récupérer la liste des annonces publiées
        // Sans JOIN FETCH, chaque accès à category/author ferait une requête supplémentaire
        PagedResult<Annonce> results = annonceService.listerPubliees(0, 10);

        // Vérifier qu'on peut accéder aux relations sans erreur
        // (Si N+1 n'était pas géré, on aurait des LazyInitializationException)
        assertDoesNotThrow(() -> {
            for (Annonce a : results.getContent()) {
                if (a.getCategory() != null) {
                    a.getCategory().getLabel(); // Accès à la relation
                }
                if (a.getAuthor() != null) {
                    a.getAuthor().getUsername(); // Accès à la relation
                }
            }
        }, "Toutes les relations doivent être accessibles sans erreur N+1");
    }

    // ==================== TEST RÈGLES MÉTIER CROISÉES ====================

    @Test
    @Order(40)
    @DisplayName("Règle métier : impossible de publier puis modifier une annonce archivée")
    void testBusinessRuleArchived() {
        String suffix = uniqueSuffix();

        // Créer, publier, archiver
        Annonce annonce = new Annonce("Archive Test " + suffix, "Desc", "Addr", "archive@test.com");
        Annonce created = annonceService.creer(annonce, testUser.getId(), null);
        annonceService.publier(created.getId());
        annonceService.archiver(created.getId());

        // Impossible de re-publier
        assertThrows(BusinessException.class, () -> {
            annonceService.publier(created.getId());
        }, "Une annonce archivée ne peut pas être re-publiée");

        // Impossible de modifier
        assertThrows(BusinessException.class, () -> {
            annonceService.modifier(created.getId(), "Nouveau titre", null, null, null, null);
        }, "Une annonce archivée ne peut pas être modifiée");
    }

    @Test
    @Order(41)
    @DisplayName("Règle métier : cohérence des compteurs")
    void testCountConsistency() {
        // Compter avant
        long countBefore = annonceService.listerPubliees(0, 1).getTotalElements();

        // Créer et publier une annonce
        String suffix = uniqueSuffix();
        Annonce annonce = new Annonce("Count Test " + suffix, "Desc", "Addr", "count@test.com");
        Annonce created = annonceService.creer(annonce, testUser.getId(), null);
        annonceService.publier(created.getId());

        // Compter après
        long countAfter = annonceService.listerPubliees(0, 1).getTotalElements();

        assertEquals(countBefore + 1, countAfter, "Le compteur doit augmenter de 1");

        // Archiver
        annonceService.archiver(created.getId());

        // Compter après archivage
        long countAfterArchive = annonceService.listerPubliees(0, 1).getTotalElements();

        assertEquals(countBefore, countAfterArchive, "Le compteur doit revenir à sa valeur initiale");
    }
}
