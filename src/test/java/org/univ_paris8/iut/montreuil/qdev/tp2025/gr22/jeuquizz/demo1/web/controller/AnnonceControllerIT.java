package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository.AnnonceRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository.CategoryRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository.UserRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.TestcontainersConfig;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.security.JwtService;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnnonceControllerIT extends TestcontainersConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String userToken;
    private String adminToken;
    private User user;
    private User admin;
    private Category category;

    @BeforeEach
    void setUp() {
        annonceRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();

        user = new User("user1", "user1@test.com", passwordEncoder.encode("password"));
        user.setRole("ROLE_USER");
        user = userRepository.save(user);

        admin = new User("admin1", "admin@test.com", passwordEncoder.encode("password"));
        admin.setRole("ROLE_ADMIN");
        admin = userRepository.save(admin);

        category = new Category("Immobilier");
        category = categoryRepository.save(category);

        userToken = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole());
        adminToken = jwtService.generateToken(admin.getId(), admin.getUsername(), admin.getRole());
    }

    // ==================== CRUD COMPLET ====================

    @Test
    @DisplayName("POST /api/annonces - Création avec token valide retourne 201")
    void creer_succes() throws Exception {
        String body = """
                {
                    "title": "Appartement T3",
                    "description": "Bel appartement lumineux",
                    "adress": "Paris 11e",
                    "mail": "contact@test.com",
                    "categoryId": %d
                }
                """.formatted(category.getId());

        mockMvc.perform(post("/api/annonces")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Appartement T3")))
                .andExpect(jsonPath("$.status", is("DRAFT")))
                .andExpect(jsonPath("$.authorUsername", is("user1")))
                .andExpect(jsonPath("$.categoryLabel", is("Immobilier")));
    }

    @Test
    @DisplayName("GET /api/annonces - Liste avec token valide retourne 200")
    void lister_succes() throws Exception {
        mockMvc.perform(get("/api/annonces")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", notNullValue()))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.totalElements", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/annonces/{id} - Détail avec token valide retourne 200")
    void detail_succes() throws Exception {
        Annonce annonce = creerAnnonce(user);

        mockMvc.perform(get("/api/annonces/" + annonce.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Test annonce")))
                .andExpect(jsonPath("$.id", is(annonce.getId().intValue())));
    }

    @Test
    @DisplayName("GET /api/annonces/{id} - Annonce inexistante retourne 404")
    void detail_inexistant() throws Exception {
        mockMvc.perform(get("/api/annonces/99999")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/annonces/{id} - Modification par l'auteur retourne 200")
    void modifier_succes() throws Exception {
        Annonce annonce = creerAnnonce(user);

        String body = """
                {
                    "title": "Titre modifié",
                    "description": "Nouvelle description"
                }
                """;

        mockMvc.perform(put("/api/annonces/" + annonce.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Titre modifié")));
    }

    @Test
    @DisplayName("POST /api/annonces/{id}/publier - Publication par l'auteur retourne 200")
    void publier_succes() throws Exception {
        Annonce annonce = creerAnnonce(user);

        mockMvc.perform(post("/api/annonces/" + annonce.getId() + "/publier")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PUBLISHED")));
    }

    @Test
    @DisplayName("POST /api/annonces/{id}/archiver - Archivage par ADMIN retourne 200")
    void archiver_parAdmin() throws Exception {
        Annonce annonce = creerAnnonce(user);
        annonce.setStatus(AnnonceStatus.PUBLISHED);
        annonceRepository.save(annonce);

        mockMvc.perform(post("/api/annonces/" + annonce.getId() + "/archiver")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ARCHIVED")));
    }

    @Test
    @DisplayName("DELETE /api/annonces/{id} - Suppression d'une annonce archivée retourne 204")
    void supprimer_succes() throws Exception {
        Annonce annonce = creerAnnonce(user);
        annonce.setStatus(AnnonceStatus.ARCHIVED);
        annonceRepository.save(annonce);

        mockMvc.perform(delete("/api/annonces/" + annonce.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());
    }

    // ==================== SECURITE : SANS TOKEN (401) ====================

    @Test
    @DisplayName("POST /api/annonces sans token retourne 401")
    void creer_sansToken() throws Exception {
        mockMvc.perform(post("/api/annonces")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /api/annonces/1 sans token retourne 401")
    void modifier_sansToken() throws Exception {
        mockMvc.perform(put("/api/annonces/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /api/annonces/1 sans token retourne 401")
    void supprimer_sansToken() throws Exception {
        mockMvc.perform(delete("/api/annonces/1"))
                .andExpect(status().isUnauthorized());
    }

    // ==================== SECURITE : TOKEN INVALIDE (401) ====================

    @Test
    @DisplayName("POST /api/annonces avec token invalide retourne 401")
    void creer_tokenInvalide() throws Exception {
        mockMvc.perform(post("/api/annonces")
                        .header("Authorization", "Bearer fake.invalid.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Test\"}"))
                .andExpect(status().isUnauthorized());
    }

    // ==================== SECURITE : ROLE INSUFFISANT (403) ====================

    @Test
    @DisplayName("POST /api/annonces/{id}/archiver par USER retourne 403")
    void archiver_parUser_interdit() throws Exception {
        Annonce annonce = creerAnnonce(user);
        annonce.setStatus(AnnonceStatus.PUBLISHED);
        annonceRepository.save(annonce);

        mockMvc.perform(post("/api/annonces/" + annonce.getId() + "/archiver")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    // ==================== REGLES METIER ====================

    @Test
    @DisplayName("PUT annonce PUBLISHED retourne 409")
    void modifier_annoncePubliee() throws Exception {
        Annonce annonce = creerAnnonce(user);
        annonce.setStatus(AnnonceStatus.PUBLISHED);
        annonceRepository.save(annonce);

        mockMvc.perform(put("/api/annonces/" + annonce.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Nouveau titre\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT annonce d'un autre utilisateur retourne 403")
    void modifier_pasAuteur() throws Exception {
        Annonce annonce = creerAnnonce(admin);

        mockMvc.perform(put("/api/annonces/" + annonce.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Nouveau titre\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE annonce non archivée retourne 409")
    void supprimer_nonArchivee() throws Exception {
        Annonce annonce = creerAnnonce(user);

        mockMvc.perform(delete("/api/annonces/" + annonce.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isConflict());
    }

    // ==================== HELPER ====================

    private Annonce creerAnnonce(User author) {
        Annonce annonce = new Annonce("Test annonce", "Description test", "Paris", "test@mail.com");
        annonce.setAuthor(author);
        annonce.setCategory(category);
        annonce.setStatus(AnnonceStatus.DRAFT);
        return annonceRepository.save(annonce);
    }
}
