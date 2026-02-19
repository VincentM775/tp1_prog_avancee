package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service de gestion de tokens stateless en mémoire.
 * Stocke l'association token → userId dans une ConcurrentHashMap.
 * Singleton pour être partagé entre le filtre de sécurité et les resources.
 */
public class TokenService {

    private static final TokenService INSTANCE = new TokenService();

    // token → userId
    private final Map<String, Long> tokens = new ConcurrentHashMap<>();

    private TokenService() {
    }

    public static TokenService getInstance() {
        return INSTANCE;
    }

    /**
     * Génère un token unique (UUID) et l'associe à un userId.
     */
    public String generateToken(Long userId) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, userId);
        return token;
    }

    /**
     * Valide un token et retourne le userId associé.
     */
    public Optional<Long> validateToken(String token) {
        if (token == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(tokens.get(token));
    }

    /**
     * Révoque un token (logout).
     */
    public void revokeToken(String token) {
        if (token != null) {
            tokens.remove(token);
        }
    }
}
