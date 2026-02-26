package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private final Key signingKey;
    private final long expiration;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration}") long expiration) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    /**
     * Génère un JWT signé contenant l'ID utilisateur et son rôle.
     */
    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valide le token et retourne les claims.
     */
    public Claims validateAndGetClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extrait le username (subject) du token.
     */
    public String getUsername(String token) {
        return validateAndGetClaims(token).getSubject();
    }

    /**
     * Extrait l'ID utilisateur du token.
     */
    public Long getUserId(String token) {
        return validateAndGetClaims(token).get("userId", Long.class);
    }

    /**
     * Extrait le rôle du token.
     */
    public String getRole(String token) {
        return validateAndGetClaims(token).get("role", String.class);
    }

    /**
     * Vérifie si le token est valide (signature + non expiré).
     */
    public boolean isTokenValid(String token) {
        try {
            validateAndGetClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
