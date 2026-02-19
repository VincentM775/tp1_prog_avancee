package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.resource;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.AnnonceService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.EntityNotFoundException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.PagedResult;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.dto.AnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.dto.CreateAnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.dto.UpdateAnnonceDTO;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires de AnnonceResource avec Mockito.
 * Le service est mocké : on teste uniquement la logique du contrôleur REST
 * (codes HTTP, payloads, appels au service).
 */
@ExtendWith(MockitoExtension.class)
class AnnonceResourceTest {

    @Mock
    private AnnonceService annonceService;

    @Mock
    private ContainerRequestContext requestContext;

    private AnnonceResource annonceResource;

    @BeforeEach
    void setUp() throws Exception {
        annonceResource = new AnnonceResource(annonceService);
        // Inject the mock requestContext via reflection
        Field contextField = AnnonceResource.class.getDeclaredField("requestContext");
        contextField.setAccessible(true);
        contextField.set(annonceResource, requestContext);
    }

    private Annonce createTestAnnonce(Long id, String title, AnnonceStatus status) {
        User author = new User("alice", "alice@test.com", "pass");
        author.setId(1L);
        Annonce a = new Annonce(title, "Description", "Adresse", "mail@test.com");
        a.setId(id);
        a.setStatus(status);
        a.setDate(new Timestamp(System.currentTimeMillis()));
        a.setAuthor(author);
        return a;
    }

    // ==================== GET /annonces ====================

    @Test
    @DisplayName("GET /annonces - 200 OK avec liste paginée")
    void testListReturns200() {
        List<Annonce> annonces = List.of(
                createTestAnnonce(1L, "Annonce 1", AnnonceStatus.PUBLISHED),
                createTestAnnonce(2L, "Annonce 2", AnnonceStatus.PUBLISHED)
        );
        PagedResult<Annonce> result = new PagedResult<>(annonces, 0, 10, 2);
        when(annonceService.listerPubliees(0, 10)).thenReturn(result);

        Response response = annonceResource.list(0, 10);

        assertEquals(200, response.getStatus());
        verify(annonceService).listerPubliees(0, 10);
    }

    // ==================== GET /annonces/{id} ====================

    @Test
    @DisplayName("GET /annonces/{id} - 200 OK quand l'annonce existe")
    void testGetByIdReturns200() {
        Annonce annonce = createTestAnnonce(1L, "Test", AnnonceStatus.PUBLISHED);
        when(annonceService.trouverParId(1L)).thenReturn(Optional.of(annonce));

        Response response = annonceResource.getById(1L);

        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
        assertInstanceOf(AnnonceDTO.class, response.getEntity());
    }

    @Test
    @DisplayName("GET /annonces/{id} - 404 quand l'annonce n'existe pas")
    void testGetByIdThrows404() {
        when(annonceService.trouverParId(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> annonceResource.getById(999L));
    }

    // ==================== POST /annonces ====================

    @Test
    @DisplayName("POST /annonces - 201 Created avec Location header")
    void testCreateReturns201() {
        when(requestContext.getProperty("userId")).thenReturn(1L);

        Annonce created = createTestAnnonce(42L, "Nouvelle annonce", AnnonceStatus.DRAFT);
        when(annonceService.creer(any(Annonce.class), eq(1L), isNull())).thenReturn(created);

        CreateAnnonceDTO dto = new CreateAnnonceDTO();
        dto.setTitle("Nouvelle annonce");

        Response response = annonceResource.create(dto);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        assertTrue(response.getLocation().toString().contains("42"));
    }

    // ==================== PUT /annonces/{id} ====================

    @Test
    @DisplayName("PUT /annonces/{id} - 200 OK avec annonce mise à jour")
    void testUpdateReturns200() {
        when(requestContext.getProperty("userId")).thenReturn(1L);

        Annonce updated = createTestAnnonce(1L, "Titre modifié", AnnonceStatus.DRAFT);
        when(annonceService.modifier(eq(1L), anyString(), isNull(), isNull(), isNull(), isNull(), eq(1L)))
                .thenReturn(updated);

        UpdateAnnonceDTO dto = new UpdateAnnonceDTO();
        dto.setTitle("Titre modifié");

        Response response = annonceResource.update(1L, dto);

        assertEquals(200, response.getStatus());
    }

    // ==================== PATCH /annonces/{id} ====================

    @Test
    @DisplayName("PATCH /annonces/{id} - 200 OK avec mise à jour partielle")
    void testPatchReturns200() {
        when(requestContext.getProperty("userId")).thenReturn(1L);

        Annonce patched = createTestAnnonce(1L, "Titre patché", AnnonceStatus.DRAFT);
        when(annonceService.modifier(eq(1L), eq("Titre patché"), isNull(), isNull(), isNull(), isNull(), eq(1L)))
                .thenReturn(patched);

        UpdateAnnonceDTO dto = new UpdateAnnonceDTO();
        dto.setTitle("Titre patché");

        Response response = annonceResource.patch(1L, dto);

        assertEquals(200, response.getStatus());
        AnnonceDTO responseDto = (AnnonceDTO) response.getEntity();
        assertEquals("Titre patché", responseDto.getTitle());
    }

    // ==================== DELETE /annonces/{id} ====================

    @Test
    @DisplayName("DELETE /annonces/{id} - 204 No Content")
    void testDeleteReturns204() {
        when(requestContext.getProperty("userId")).thenReturn(1L);
        doNothing().when(annonceService).supprimer(1L, 1L);

        Response response = annonceResource.delete(1L);

        assertEquals(204, response.getStatus());
        assertNull(response.getEntity());
        verify(annonceService).supprimer(1L, 1L);
    }

    // ==================== POST /annonces/{id}/publier ====================

    @Test
    @DisplayName("POST /annonces/{id}/publier - 200 OK")
    void testPublierReturns200() {
        when(requestContext.getProperty("userId")).thenReturn(1L);

        Annonce published = createTestAnnonce(1L, "Test", AnnonceStatus.PUBLISHED);
        when(annonceService.publier(1L, 1L)).thenReturn(published);

        Response response = annonceResource.publier(1L);

        assertEquals(200, response.getStatus());
        AnnonceDTO dto = (AnnonceDTO) response.getEntity();
        assertEquals("PUBLISHED", dto.getStatus());
    }

    // ==================== POST /annonces/{id}/archiver ====================

    @Test
    @DisplayName("POST /annonces/{id}/archiver - 200 OK")
    void testArchiverReturns200() {
        when(requestContext.getProperty("userId")).thenReturn(1L);

        Annonce archived = createTestAnnonce(1L, "Test", AnnonceStatus.ARCHIVED);
        when(annonceService.archiver(1L, 1L)).thenReturn(archived);

        Response response = annonceResource.archiver(1L);

        assertEquals(200, response.getStatus());
        AnnonceDTO dto = (AnnonceDTO) response.getEntity();
        assertEquals("ARCHIVED", dto.getStatus());
    }
}
