package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.persistence.EntityManagerUtil;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration du Repository avec H2 In-Memory.
 *
 * Le jeu de données test-data.sql est chargé automatiquement.
 * Contient : 3 users, 3 categories, 7 annonces (5 PUBLISHED, 1 DRAFT, 1 ARCHIVED).
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnnonceRepositoryIT {

    private static AnnonceRepository annonceRepository;

    @BeforeAll
    static void setUp() {
        EntityManagerUtil.init("testPU");
        annonceRepository = new AnnonceRepository();
    }

    @AfterAll
    static void tearDown() {
        EntityManagerUtil.close();
    }

    // ==================== TESTS CRUD ====================

    @Test
    @Order(1)
    @DisplayName("findById() - Annonce existante")
    void testFindByIdExisting() {
        Optional<Annonce> found = annonceRepository.findById(1L);
        assertTrue(found.isPresent());
        assertEquals("Appartement Paris 15", found.get().getTitle());
    }

    @Test
    @Order(2)
    @DisplayName("findById() - Annonce inexistante retourne empty")
    void testFindByIdNotFound() {
        Optional<Annonce> found = annonceRepository.findById(999L);
        assertFalse(found.isPresent());
    }

    @Test
    @Order(3)
    @DisplayName("save() - Création d'une nouvelle annonce")
    void testSaveNewAnnonce() {
        EntityManager em = EntityManagerUtil.getEntityManager();
        User author = em.find(User.class, 1L);
        em.close();

        Annonce annonce = new Annonce("Nouvelle annonce test", "Description test", "Adresse", "new@test.com");
        annonce.setAuthor(author);
        annonce.setStatus(AnnonceStatus.DRAFT);

        Annonce saved = annonceRepository.save(annonce);

        assertNotNull(saved.getId());
        assertEquals("Nouvelle annonce test", saved.getTitle());
    }

    // ==================== TESTS RECHERCHE ====================

    @Test
    @Order(10)
    @DisplayName("findByStatus() - Filtre par statut PUBLISHED")
    void testFindByStatusPublished() {
        List<Annonce> published = annonceRepository.findByStatus(AnnonceStatus.PUBLISHED);
        assertEquals(5, published.size());
        published.forEach(a -> assertEquals(AnnonceStatus.PUBLISHED, a.getStatus()));
    }

    @Test
    @Order(11)
    @DisplayName("findByStatus() - Filtre par statut DRAFT")
    void testFindByStatusDraft() {
        List<Annonce> drafts = annonceRepository.findByStatus(AnnonceStatus.DRAFT);
        assertFalse(drafts.isEmpty());
        drafts.forEach(a -> assertEquals(AnnonceStatus.DRAFT, a.getStatus()));
    }

    @Test
    @Order(12)
    @DisplayName("findByKeyword() - Recherche dans titre et description")
    void testFindByKeyword() {
        List<Annonce> results = annonceRepository.findByKeyword("appartement");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(a ->
                a.getTitle().toLowerCase().contains("appartement")
                || a.getDescription().toLowerCase().contains("appartement")));
    }

    @Test
    @Order(13)
    @DisplayName("findByKeyword() - Mot-clé inexistant retourne liste vide")
    void testFindByKeywordNoResult() {
        List<Annonce> results = annonceRepository.findByKeyword("xyzinexistant");
        assertTrue(results.isEmpty());
    }

    @Test
    @Order(14)
    @DisplayName("findByCategoryId() - Filtre par catégorie")
    void testFindByCategoryId() {
        // Catégorie 1 = Immobilier (annonces 1, 2, 6)
        List<Annonce> immobilier = annonceRepository.findByCategoryId(1L);
        assertFalse(immobilier.isEmpty());
        immobilier.forEach(a -> assertEquals(1L, a.getCategory().getId()));
    }

    // ==================== TESTS PAGINATION ====================

    @Test
    @Order(20)
    @DisplayName("Pagination - Page 0, taille 2")
    void testPaginationPage0() {
        List<Annonce> page0 = annonceRepository.findPublished(0, 2);
        assertEquals(2, page0.size());
    }

    @Test
    @Order(21)
    @DisplayName("Pagination - Page 1, taille 2")
    void testPaginationPage1() {
        List<Annonce> page1 = annonceRepository.findPublished(1, 2);
        assertEquals(2, page1.size());
    }

    @Test
    @Order(22)
    @DisplayName("Pagination - Dernière page (partielle)")
    void testPaginationLastPage() {
        // 5 annonces PUBLISHED, page 2 avec taille 2 = 1 élément
        List<Annonce> lastPage = annonceRepository.findPublished(2, 2);
        assertEquals(1, lastPage.size());
    }

    @Test
    @Order(23)
    @DisplayName("Pagination - Page au-delà des résultats retourne liste vide")
    void testPaginationBeyondResults() {
        List<Annonce> emptyPage = annonceRepository.findPublished(100, 2);
        assertTrue(emptyPage.isEmpty());
    }

    @Test
    @Order(24)
    @DisplayName("Pagination - Les pages contiennent des éléments différents")
    void testPaginationNoDuplicates() {
        List<Annonce> page0 = annonceRepository.findPublished(0, 2);
        List<Annonce> page1 = annonceRepository.findPublished(1, 2);

        List<Long> idsPage0 = page0.stream().map(Annonce::getId).toList();
        List<Long> idsPage1 = page1.stream().map(Annonce::getId).toList();

        // Aucun ID en commun
        idsPage0.forEach(id -> assertFalse(idsPage1.contains(id),
                "L'ID " + id + " ne doit pas apparaître dans les deux pages"));
    }

    @Test
    @Order(25)
    @DisplayName("countByStatus() - Comptage cohérent avec pagination")
    void testCountConsistentWithPagination() {
        long totalPublished = annonceRepository.countByStatus(AnnonceStatus.PUBLISHED);
        assertEquals(5, totalPublished);
    }

    // ==================== TESTS RECHERCHE COMBINÉE ====================

    @Test
    @Order(30)
    @DisplayName("search() - Critères combinés (keyword + category + status)")
    void testSearchCombined() {
        // Chercher "Peugeot" dans catégorie Véhicules (id=2), statut PUBLISHED
        List<Annonce> results = annonceRepository.search("Peugeot", 2L, AnnonceStatus.PUBLISHED, 0, 10);
        assertEquals(1, results.size());
        assertEquals("Voiture Peugeot 308", results.get(0).getTitle());
    }

    @Test
    @Order(31)
    @DisplayName("countSearch() - Comptage avec critères combinés")
    void testCountSearch() {
        // Toutes les annonces PUBLISHED dans catégorie Véhicules
        long count = annonceRepository.countSearch(null, 2L, AnnonceStatus.PUBLISHED);
        assertEquals(2, count); // Peugeot 308 + Renault Clio
    }

    // ==================== TEST SUPPRESSION ====================

    @Test
    @Order(100)
    @DisplayName("deleteById() - Suppression effective")
    void testDeleteById() {
        // Créer puis supprimer
        EntityManager em = EntityManagerUtil.getEntityManager();
        User author = em.find(User.class, 1L);
        em.close();

        Annonce toDelete = new Annonce("A supprimer", "Desc", "Addr", "del@test.com");
        toDelete.setAuthor(author);
        toDelete.setStatus(AnnonceStatus.DRAFT);
        Annonce saved = annonceRepository.save(toDelete);

        annonceRepository.deleteById(saved.getId());

        Optional<Annonce> deleted = annonceRepository.findById(saved.getId());
        assertFalse(deleted.isPresent());
    }
}
