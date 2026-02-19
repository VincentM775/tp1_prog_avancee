package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.*;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires des DTOs et de leur mapping Entity <-> DTO.
 */
class AnnonceDTOTest {

    // ==================== AnnonceDTO (Entity → DTO) ====================

    @Test
    @DisplayName("fromEntity() - Mapping complet avec toutes les relations")
    void testFromEntityComplete() {
        User author = new User("alice", "alice@test.com", "pass");
        author.setId(1L);

        Category category = new Category("Immobilier");
        category.setId(10L);

        Annonce annonce = new Annonce("Titre", "Description", "Adresse", "mail@test.com");
        annonce.setId(42L);
        annonce.setStatus(AnnonceStatus.PUBLISHED);
        annonce.setDate(new Timestamp(System.currentTimeMillis()));
        annonce.setAuthor(author);
        annonce.setCategory(category);

        AnnonceDTO dto = AnnonceDTO.fromEntity(annonce);

        assertEquals(42L, dto.getId());
        assertEquals("Titre", dto.getTitle());
        assertEquals("Description", dto.getDescription());
        assertEquals("Adresse", dto.getAdress());
        assertEquals("mail@test.com", dto.getMail());
        assertEquals("PUBLISHED", dto.getStatus());
        assertNotNull(dto.getDateCreation());
        assertEquals(1L, dto.getAuthorId());
        assertEquals("alice", dto.getAuthorUsername());
        assertEquals(10L, dto.getCategoryId());
        assertEquals("Immobilier", dto.getCategoryLabel());
    }

    @Test
    @DisplayName("fromEntity() - Sans relations (author et category null)")
    void testFromEntityWithoutRelations() {
        Annonce annonce = new Annonce("Titre seul", null, null, null);
        annonce.setId(1L);
        annonce.setStatus(AnnonceStatus.DRAFT);

        AnnonceDTO dto = AnnonceDTO.fromEntity(annonce);

        assertEquals(1L, dto.getId());
        assertEquals("Titre seul", dto.getTitle());
        assertNull(dto.getAuthorId());
        assertNull(dto.getAuthorUsername());
        assertNull(dto.getCategoryId());
        assertNull(dto.getCategoryLabel());
    }

    // ==================== AnnonceDTO Builder ====================

    @Test
    @DisplayName("Builder - Construction fluide")
    void testBuilder() {
        AnnonceDTO dto = new AnnonceDTO.Builder()
                .id(1L)
                .title("Mon titre")
                .status("DRAFT")
                .authorUsername("bob")
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("Mon titre", dto.getTitle());
        assertEquals("DRAFT", dto.getStatus());
        assertEquals("bob", dto.getAuthorUsername());
    }

    // ==================== CreateAnnonceDTO (DTO → Entity) ====================

    @Test
    @DisplayName("toEntity() - Conversion DTO vers Entity")
    void testToEntity() {
        CreateAnnonceDTO dto = new CreateAnnonceDTO();
        dto.setTitle("Nouveau titre");
        dto.setDescription("Nouvelle description");
        dto.setAdress("Paris");
        dto.setMail("contact@test.com");

        Annonce entity = dto.toEntity();

        assertEquals("Nouveau titre", entity.getTitle());
        assertEquals("Nouvelle description", entity.getDescription());
        assertEquals("Paris", entity.getAdress());
        assertEquals("contact@test.com", entity.getMail());
        // L'entité ne doit pas avoir d'ID, d'auteur, ni de catégorie (ajoutés par le service)
        assertNull(entity.getId());
        assertNull(entity.getAuthor());
        assertNull(entity.getCategory());
    }

    // ==================== ApiError ====================

    @Test
    @DisplayName("ApiError - Construction avec détails de validation")
    void testApiError() {
        ApiError error = new ApiError(400, "Bad Request", "Validation échouée",
                java.util.List.of(
                        new ApiError.FieldError("title", "Le titre est obligatoire"),
                        new ApiError.FieldError("mail", "L'email doit être valide")
                ));

        assertEquals(400, error.getStatus());
        assertEquals("Bad Request", error.getError());
        assertEquals("Validation échouée", error.getMessage());
        assertNotNull(error.getTimestamp());
        assertEquals(2, error.getDetails().size());
        assertEquals("title", error.getDetails().get(0).getField());
    }
}
