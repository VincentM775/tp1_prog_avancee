package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository;

import org.junit.jupiter.api.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration pour les repositories.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RepositoryTest {

    private static UserRepository userRepository;
    private static CategoryRepository categoryRepository;
    private static AnnonceRepository annonceRepository;

    // Données de test réutilisables
    private static User testUser;
    private static Category testCategory;

    private String uniqueSuffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @BeforeAll
    static void setUpRepositories() {
        userRepository = new UserRepository();
        categoryRepository = new CategoryRepository();
        annonceRepository = new AnnonceRepository();
    }

    // ==================== TESTS USER REPOSITORY ====================

    @Test
    @Order(1)
    @DisplayName("UserRepository - save() et findById()")
    void testUserSaveAndFind() {
        String suffix = uniqueSuffix();
        User user = new User("repouser_" + suffix, "repo_" + suffix + "@test.com", "password");

        // Test save
        User saved = userRepository.save(user);
        assertNotNull(saved.getId(), "L'ID doit être généré après save");

        // Test findById
        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent(), "L'utilisateur doit être trouvé");
        assertEquals("repouser_" + suffix, found.get().getUsername());

        // Garder pour les tests suivants
        testUser = saved;
    }

    @Test
    @Order(2)
    @DisplayName("UserRepository - findByUsername()")
    void testUserFindByUsername() {
        Optional<User> found = userRepository.findByUsername(testUser.getUsername());
        assertTrue(found.isPresent(), "L'utilisateur doit être trouvé par username");
        assertEquals(testUser.getEmail(), found.get().getEmail());
    }

    @Test
    @Order(3)
    @DisplayName("UserRepository - findByEmail()")
    void testUserFindByEmail() {
        Optional<User> found = userRepository.findByEmail(testUser.getEmail());
        assertTrue(found.isPresent(), "L'utilisateur doit être trouvé par email");
        assertEquals(testUser.getUsername(), found.get().getUsername());
    }

    @Test
    @Order(4)
    @DisplayName("UserRepository - existsByUsername()")
    void testUserExistsByUsername() {
        assertTrue(userRepository.existsByUsername(testUser.getUsername()));
        assertFalse(userRepository.existsByUsername("inexistant_user_xyz"));
    }

    @Test
    @Order(5)
    @DisplayName("UserRepository - findByUsernameContaining()")
    void testUserFindByUsernameContaining() {
        // Le username contient "repouser_"
        List<User> found = userRepository.findByUsernameContaining("repouser");
        assertFalse(found.isEmpty(), "Au moins un utilisateur doit être trouvé");
    }

    // ==================== TESTS CATEGORY REPOSITORY ====================

    @Test
    @Order(10)
    @DisplayName("CategoryRepository - save() et findById()")
    void testCategorySaveAndFind() {
        String suffix = uniqueSuffix();
        Category category = new Category("TestCategorie_" + suffix);

        Category saved = categoryRepository.save(category);
        assertNotNull(saved.getId());

        Optional<Category> found = categoryRepository.findById(saved.getId());
        assertTrue(found.isPresent());

        testCategory = saved;
    }

    @Test
    @Order(11)
    @DisplayName("CategoryRepository - findByLabel()")
    void testCategoryFindByLabel() {
        Optional<Category> found = categoryRepository.findByLabel(testCategory.getLabel());
        assertTrue(found.isPresent());
        assertEquals(testCategory.getId(), found.get().getId());
    }

    @Test
    @Order(12)
    @DisplayName("CategoryRepository - findAllOrderByLabel()")
    void testCategoryFindAllOrderByLabel() {
        List<Category> categories = categoryRepository.findAllOrderByLabel();
        assertFalse(categories.isEmpty());

        // Vérifier que la liste est triée
        for (int i = 1; i < categories.size(); i++) {
            assertTrue(
                categories.get(i - 1).getLabel().compareToIgnoreCase(categories.get(i).getLabel()) <= 0,
                "Les catégories doivent être triées par label"
            );
        }
    }

    // ==================== TESTS ANNONCE REPOSITORY ====================

    @Test
    @Order(20)
    @DisplayName("AnnonceRepository - save() avec relations")
    void testAnnonceSaveWithRelations() {
        Annonce annonce = new Annonce(
            "Voiture occasion",
            "Belle voiture à vendre, bon état",
            "Paris",
            "contact@test.com"
        );
        annonce.setAuthor(testUser);
        annonce.setCategory(testCategory);
        annonce.setStatus(AnnonceStatus.PUBLISHED);

        Annonce saved = annonceRepository.save(annonce);
        assertNotNull(saved.getId());

        Optional<Annonce> found = annonceRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Voiture occasion", found.get().getTitle());
    }

    @Test
    @Order(21)
    @DisplayName("AnnonceRepository - findByKeyword()")
    void testAnnonceFindByKeyword() {
        // Recherche dans le titre
        List<Annonce> foundByTitle = annonceRepository.findByKeyword("voiture");
        assertFalse(foundByTitle.isEmpty(), "Doit trouver par mot-clé dans le titre");

        // Recherche dans la description
        List<Annonce> foundByDesc = annonceRepository.findByKeyword("bon état");
        assertFalse(foundByDesc.isEmpty(), "Doit trouver par mot-clé dans la description");

        // Recherche inexistante
        List<Annonce> notFound = annonceRepository.findByKeyword("xyzinexistant123");
        assertTrue(notFound.isEmpty(), "Ne doit rien trouver avec un mot inexistant");
    }

    @Test
    @Order(22)
    @DisplayName("AnnonceRepository - findByStatus()")
    void testAnnonceFindByStatus() {
        List<Annonce> published = annonceRepository.findByStatus(AnnonceStatus.PUBLISHED);
        assertFalse(published.isEmpty());

        // Vérifier que toutes les annonces ont le bon statut
        for (Annonce a : published) {
            assertEquals(AnnonceStatus.PUBLISHED, a.getStatus());
        }
    }

    @Test
    @Order(23)
    @DisplayName("AnnonceRepository - findByCategoryId()")
    void testAnnonceFindByCategoryId() {
        List<Annonce> annonces = annonceRepository.findByCategoryId(testCategory.getId());
        assertFalse(annonces.isEmpty());

        for (Annonce a : annonces) {
            assertEquals(testCategory.getId(), a.getCategory().getId());
        }
    }

    @Test
    @Order(24)
    @DisplayName("AnnonceRepository - search() avec critères combinés")
    void testAnnonceSearch() {
        // Recherche avec tous les critères
        List<Annonce> results = annonceRepository.search(
            "voiture",
            testCategory.getId(),
            AnnonceStatus.PUBLISHED,
            0, 10
        );
        assertFalse(results.isEmpty());

        // Vérifier que les résultats correspondent aux critères
        for (Annonce a : results) {
            assertTrue(
                a.getTitle().toLowerCase().contains("voiture") ||
                a.getDescription().toLowerCase().contains("voiture")
            );
            assertEquals(testCategory.getId(), a.getCategory().getId());
            assertEquals(AnnonceStatus.PUBLISHED, a.getStatus());
        }
    }

    @Test
    @Order(25)
    @DisplayName("AnnonceRepository - pagination")
    void testAnnoncePagination() {
        // Créer plusieurs annonces pour tester la pagination
        String suffix = uniqueSuffix();
        for (int i = 0; i < 5; i++) {
            Annonce a = new Annonce("Test Pagination " + i + "_" + suffix, "Description " + i, "Adresse", "mail@test.com");
            a.setStatus(AnnonceStatus.PUBLISHED);
            annonceRepository.save(a);
        }

        // Récupérer page par page (2 éléments par page)
        List<Annonce> page0 = annonceRepository.findPublished(0, 2);
        List<Annonce> page1 = annonceRepository.findPublished(1, 2);

        assertEquals(2, page0.size(), "La page 0 doit contenir 2 éléments");
        assertEquals(2, page1.size(), "La page 1 doit contenir 2 éléments");

        // Vérifier que ce ne sont pas les mêmes annonces
        assertNotEquals(page0.get(0).getId(), page1.get(0).getId());
    }

    @Test
    @Order(26)
    @DisplayName("AnnonceRepository - countSearch()")
    void testAnnonceCountSearch() {
        long totalPublished = annonceRepository.countByStatus(AnnonceStatus.PUBLISHED);
        assertTrue(totalPublished > 0);

        long countWithKeyword = annonceRepository.countSearch("voiture", null, AnnonceStatus.PUBLISHED);
        assertTrue(countWithKeyword > 0);
        assertTrue(countWithKeyword <= totalPublished);
    }

    // ==================== TESTS DELETE ====================

    @Test
    @Order(100)
    @DisplayName("Repository - delete()")
    void testDelete() {
        String suffix = uniqueSuffix();
        // IMPORTANT : Le mot de passe doit avoir au moins 6 caractères (@Size(min=6) dans User)
        User userToDelete = new User("todelete_" + suffix, "delete_" + suffix + "@test.com", "password123");
        userToDelete = userRepository.save(userToDelete);
        Long userId = userToDelete.getId();

        // Supprimer
        userRepository.deleteById(userId);

        // Vérifier la suppression
        Optional<User> deleted = userRepository.findById(userId);
        assertFalse(deleted.isPresent(), "L'utilisateur doit être supprimé");
    }
}
