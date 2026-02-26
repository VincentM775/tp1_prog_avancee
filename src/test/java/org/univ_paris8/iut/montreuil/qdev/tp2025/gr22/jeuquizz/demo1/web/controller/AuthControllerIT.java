package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository.UserRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.TestcontainersConfig;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.dto.LoginRequest;


import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIT extends TestcontainersConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User user = new User("testuser", "test@email.com", passwordEncoder.encode("password123"));
        user.setRole("ROLE_USER");
        userRepository.save(user);
    }

    // ==================== LOGIN ====================

    @Test
    @DisplayName("POST /api/auth/login - Login réussi retourne un token")
    void login_succes() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.token", not(emptyString())));
    }

    @Test
    @DisplayName("POST /api/auth/login - Mot de passe incorrect retourne 400")
    void login_mauvaisMotDePasse() throws Exception {
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login - Utilisateur inexistant retourne 400")
    void login_utilisateurInexistant() throws Exception {
        LoginRequest request = new LoginRequest("unknown", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login - Champs vides retourne 400")
    void login_champsVides() throws Exception {
        LoginRequest request = new LoginRequest("", "");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ==================== ENDPOINTS PROTEGES ====================

    @Test
    @DisplayName("GET /api/annonces sans token retourne 401")
    void endpointProtege_sansToken() throws Exception {
        mockMvc.perform(get("/api/annonces"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/annonces avec token invalide retourne 401")
    void endpointProtege_tokenInvalide() throws Exception {
        mockMvc.perform(get("/api/annonces")
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/annonces avec token expiré retourne 401")
    void endpointProtege_tokenExpire() throws Exception {
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0IiwidXNlcklkIjoxLCJyb2xlIjoiUk9MRV9VU0VSIiwiaWF0IjoxNjAwMDAwMDAwLCJleHAiOjE2MDAwMDAwMDF9.invalid";

        mockMvc.perform(get("/api/annonces")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    // ==================== ENDPOINTS PUBLICS ====================

    @Test
    @DisplayName("GET /api/meta/annonces est accessible sans token")
    void endpointPublic_meta() throws Exception {
        mockMvc.perform(get("/api/meta/annonces"))
                .andExpect(status().isOk());
    }
}
