package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.persistence.EntityManagerUtil;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.TokenService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.filter.AuthenticationFilter;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.mapper.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration REST avec Jersey Test Framework + H2.
 * Lance un serveur embarqué Grizzly et exécute de vraies requêtes HTTP.
 *
 * Vérifie les payloads JSON ET les codes HTTP (y compris cas d'erreur).
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnnonceResourceIT extends JerseyTest {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static String authToken;

    @Override
    protected Application configure() {
        // Initialiser H2 pour les tests
        EntityManagerUtil.init("testPU");

        // Générer un token pour l'utilisateur alice (id=1)
        authToken = TokenService.getInstance().generateToken(1L);

        return new ResourceConfig()
                .register(AnnonceResource.class)
                .register(AuthResource.class)
                .register(HelloWorldResource.class)
                .register(ParamsResource.class)
                .register(AuthenticationFilter.class)
                .register(EntityNotFoundExceptionMapper.class)
                .register(BusinessExceptionMapper.class)
                .register(ConflictExceptionMapper.class)
                .register(ForbiddenExceptionMapper.class)
                .register(ConstraintViolationExceptionMapper.class)
                .register(OptimisticLockExceptionMapper.class)
                .register(GenericExceptionMapper.class);
    }

    @AfterAll
    static void cleanup() {
        EntityManagerUtil.close();
    }

    // ==================== ENDPOINTS PUBLICS ====================

    @Test
    @Order(1)
    @DisplayName("GET /helloWorld - 200 OK sans authentification")
    void testHelloWorld() {
        Response response = target("/helloWorld").request().get();

        assertEquals(200, response.getStatus());
        String json = response.readEntity(String.class);
        assertTrue(json.contains("Hello World"));
    }

    @Test
    @Order(2)
    @DisplayName("GET /params?nom=Jean&age=25 - 200 OK avec QueryParams")
    void testQueryParams() {
        Response response = target("/params")
                .queryParam("nom", "Jean")
                .queryParam("age", 25)
                .request().get();

        assertEquals(200, response.getStatus());
        String json = response.readEntity(String.class);
        assertTrue(json.contains("Jean"));
        assertTrue(json.contains("QueryParams"));
    }

    @Test
    @Order(3)
    @DisplayName("GET /params/Jean/25 - 200 OK avec PathParams")
    void testPathParams() {
        Response response = target("/params/Jean/25").request().get();

        assertEquals(200, response.getStatus());
        String json = response.readEntity(String.class);
        assertTrue(json.contains("Jean"));
        assertTrue(json.contains("PathParams"));
    }

    // ==================== GET /annonces (public) ====================

    @Test
    @Order(10)
    @DisplayName("GET /annonces - 200 OK avec pagination")
    void testListAnnonces() throws Exception {
        Response response = target("/annonces")
                .queryParam("page", 0)
                .queryParam("size", 2)
                .request().get();

        assertEquals(200, response.getStatus());

        JsonNode json = mapper.readTree(response.readEntity(String.class));
        assertEquals(2, json.get("content").size());
        assertTrue(json.get("totalElements").asInt() >= 5);
        assertEquals(0, json.get("page").asInt());
        assertEquals(2, json.get("size").asInt());
        assertTrue(json.has("hasNext"));
        assertTrue(json.has("hasPrevious"));
    }

    @Test
    @Order(11)
    @DisplayName("GET /annonces/{id} - 200 OK avec payload complet")
    void testGetAnnonceById() throws Exception {
        Response response = target("/annonces/1").request().get();

        assertEquals(200, response.getStatus());

        JsonNode json = mapper.readTree(response.readEntity(String.class));
        assertEquals(1, json.get("id").asInt());
        assertEquals("Appartement Paris 15", json.get("title").asText());
        assertEquals("PUBLISHED", json.get("status").asText());
        assertNotNull(json.get("authorUsername"));
        assertNotNull(json.get("categoryLabel"));
    }

    @Test
    @Order(12)
    @DisplayName("GET /annonces/999 - 404 Not Found avec ApiError")
    void testGetAnnonceNotFound() throws Exception {
        Response response = target("/annonces/999").request().get();

        assertEquals(404, response.getStatus());

        JsonNode json = mapper.readTree(response.readEntity(String.class));
        assertEquals(404, json.get("status").asInt());
        assertEquals("Not Found", json.get("error").asText());
        assertNotNull(json.get("message"));
        assertNotNull(json.get("timestamp"));
    }

    // ==================== SÉCURITÉ ====================

    @Test
    @Order(20)
    @DisplayName("POST /annonces sans token - 401 Unauthorized")
    void testCreateWithoutToken() throws Exception {
        String body = """
                {"title": "Test", "description": "Desc"}
                """;

        Response response = target("/annonces")
                .request()
                .post(Entity.entity(body, MediaType.APPLICATION_JSON));

        assertEquals(401, response.getStatus());

        JsonNode json = mapper.readTree(response.readEntity(String.class));
        assertEquals(401, json.get("status").asInt());
        assertEquals("Unauthorized", json.get("error").asText());
    }

    @Test
    @Order(21)
    @DisplayName("POST /annonces avec token invalide - 401 Unauthorized")
    void testCreateWithInvalidToken() {
        Response response = target("/annonces")
                .request()
                .header("Authorization", "Bearer token-bidon-invalide")
                .post(Entity.entity("{\"title\":\"Test\"}", MediaType.APPLICATION_JSON));

        assertEquals(401, response.getStatus());
    }

    // ==================== POST /annonces (authentifié) ====================

    @Test
    @Order(30)
    @DisplayName("POST /annonces - 201 Created avec Location")
    void testCreateAnnonce() throws Exception {
        String body = """
                {"title": "Annonce IT Test", "description": "Desc", "adress": "Paris", "mail": "it@test.com"}
                """;

        Response response = target("/annonces")
                .request()
                .header("Authorization", "Bearer " + authToken)
                .post(Entity.entity(body, MediaType.APPLICATION_JSON));

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());

        JsonNode json = mapper.readTree(response.readEntity(String.class));
        assertNotNull(json.get("id"));
        assertEquals("Annonce IT Test", json.get("title").asText());
        assertEquals("DRAFT", json.get("status").asText());
    }

    @Test
    @Order(31)
    @DisplayName("POST /annonces avec titre vide - 400 Bad Request (validation)")
    void testCreateAnnonceValidationError() throws Exception {
        String body = """
                {"title": "", "description": "Desc"}
                """;

        Response response = target("/annonces")
                .request()
                .header("Authorization", "Bearer " + authToken)
                .post(Entity.entity(body, MediaType.APPLICATION_JSON));

        // Bean Validation échoue → 400
        assertEquals(400, response.getStatus());
    }

    // ==================== PUT /annonces/{id} ====================

    @Test
    @Order(40)
    @DisplayName("PUT /annonces/{id} sur annonce DRAFT - 200 OK")
    void testUpdateAnnonce() throws Exception {
        // L'annonce 6 est DRAFT et appartient à alice (id=1)
        String body = """
                {"title": "Titre mis a jour"}
                """;

        Response response = target("/annonces/6")
                .request()
                .header("Authorization", "Bearer " + authToken)
                .put(Entity.entity(body, MediaType.APPLICATION_JSON));

        assertEquals(200, response.getStatus());

        JsonNode json = mapper.readTree(response.readEntity(String.class));
        assertEquals("Titre mis a jour", json.get("title").asText());
    }

    @Test
    @Order(41)
    @DisplayName("PUT /annonces/{id} sur annonce PUBLISHED - 409 Conflict")
    void testUpdatePublishedAnnonce() throws Exception {
        // L'annonce 1 est PUBLISHED et appartient à alice
        String body = """
                {"title": "Modification interdite"}
                """;

        Response response = target("/annonces/1")
                .request()
                .header("Authorization", "Bearer " + authToken)
                .put(Entity.entity(body, MediaType.APPLICATION_JSON));

        assertEquals(409, response.getStatus());
    }

    @Test
    @Order(42)
    @DisplayName("PUT /annonces/{id} par un non-auteur - 403 Forbidden")
    void testUpdateByNonAuthor() throws Exception {
        // L'annonce 3 appartient à bob (id=2), on est authentifié en tant qu'alice (id=1)
        String body = """
                {"title": "Je ne suis pas l'auteur"}
                """;

        Response response = target("/annonces/3")
                .request()
                .header("Authorization", "Bearer " + authToken)
                .put(Entity.entity(body, MediaType.APPLICATION_JSON));

        assertEquals(403, response.getStatus());

        JsonNode json = mapper.readTree(response.readEntity(String.class));
        assertEquals("Forbidden", json.get("error").asText());
    }

    // ==================== DELETE /annonces/{id} ====================

    @Test
    @Order(50)
    @DisplayName("DELETE /annonces/{id} non archivée - 409 Conflict")
    void testDeleteNonArchivedAnnonce() {
        // L'annonce 6 est DRAFT (pas archivée), appartient à alice
        Response response = target("/annonces/6")
                .request()
                .header("Authorization", "Bearer " + authToken)
                .delete();

        assertEquals(409, response.getStatus());
    }

    // ==================== AUTH ====================

    @Test
    @Order(60)
    @DisplayName("POST /auth/login - 200 OK avec token")
    void testLogin() throws Exception {
        String body = """
                {"username": "alice", "password": "password123"}
                """;

        Response response = target("/auth/login")
                .request()
                .post(Entity.entity(body, MediaType.APPLICATION_JSON));

        assertEquals(200, response.getStatus());

        JsonNode json = mapper.readTree(response.readEntity(String.class));
        assertNotNull(json.get("token"));
        assertEquals(1, json.get("userId").asInt());
        assertEquals("alice", json.get("username").asText());
    }

    @Test
    @Order(61)
    @DisplayName("POST /auth/login avec mauvais mot de passe - 400 Bad Request")
    void testLoginWrongPassword() {
        String body = """
                {"username": "alice", "password": "wrong"}
                """;

        Response response = target("/auth/login")
                .request()
                .post(Entity.entity(body, MediaType.APPLICATION_JSON));

        assertEquals(400, response.getStatus());
    }

    @Test
    @Order(62)
    @DisplayName("POST /auth/login utilisateur inexistant - 400 Bad Request")
    void testLoginUserNotFound() {
        String body = """
                {"username": "inconnu", "password": "pass"}
                """;

        Response response = target("/auth/login")
                .request()
                .post(Entity.entity(body, MediaType.APPLICATION_JSON));

        assertEquals(400, response.getStatus());
    }
}
