package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service;

import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires du TokenService (pas besoin de BDD).
 */
class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = TokenService.getInstance();
    }

    @Test
    @DisplayName("generateToken() - Retourne un token non null")
    void testGenerateToken() {
        String token = tokenService.generateToken(1L);
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("generateToken() - Tokens uniques pour chaque appel")
    void testTokensAreUnique() {
        String token1 = tokenService.generateToken(1L);
        String token2 = tokenService.generateToken(1L);
        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("validateToken() - Token valide retourne le userId")
    void testValidateValidToken() {
        String token = tokenService.generateToken(42L);

        Optional<Long> userId = tokenService.validateToken(token);

        assertTrue(userId.isPresent());
        assertEquals(42L, userId.get());
    }

    @Test
    @DisplayName("validateToken() - Token invalide retourne empty")
    void testValidateInvalidToken() {
        Optional<Long> userId = tokenService.validateToken("token-bidon");
        assertFalse(userId.isPresent());
    }

    @Test
    @DisplayName("validateToken() - Token null retourne empty")
    void testValidateNullToken() {
        Optional<Long> userId = tokenService.validateToken(null);
        assertFalse(userId.isPresent());
    }

    @Test
    @DisplayName("revokeToken() - Token révoqué n'est plus valide")
    void testRevokeToken() {
        String token = tokenService.generateToken(1L);
        assertTrue(tokenService.validateToken(token).isPresent());

        tokenService.revokeToken(token);

        assertFalse(tokenService.validateToken(token).isPresent());
    }

    @Test
    @DisplayName("revokeToken() - Révoquer un token null ne lève pas d'exception")
    void testRevokeNullToken() {
        assertDoesNotThrow(() -> tokenService.revokeToken(null));
    }
}
