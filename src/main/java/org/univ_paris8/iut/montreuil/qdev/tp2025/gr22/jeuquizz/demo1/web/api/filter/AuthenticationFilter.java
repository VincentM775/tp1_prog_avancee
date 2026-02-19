package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.TokenService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.dto.ApiError;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import java.util.Optional;

/**
 * Filtre JAX-RS de sécurité stateless.
 * Vérifie le header Authorization: Bearer {token} sur les endpoints protégés.
 * Les endpoints publics (helloWorld, params, auth/login, GET annonces) sont exclus.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private final TokenService tokenService = TokenService.getInstance();

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();

        // Endpoints publics : pas de token requis
        if (isPublicEndpoint(path, method)) {
            return;
        }

        // Extraction du token depuis le header Authorization
        String authHeader = requestContext.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            abortUnauthorized(requestContext, "Token d'authentification manquant");
            return;
        }

        String token = authHeader.substring(7);
        Optional<Long> userId = tokenService.validateToken(token);

        if (userId.isEmpty()) {
            abortUnauthorized(requestContext, "Token d'authentification invalide ou expiré");
            return;
        }

        // Stocke le userId dans le contexte pour les resources en aval
        requestContext.setProperty("userId", userId.get());
    }

    /**
     * Détermine si un endpoint est public (pas de token requis).
     */
    private boolean isPublicEndpoint(String path, String method) {
        // Auth endpoints (login/logout)
        if (path.startsWith("auth/")) {
            return true;
        }
        // HelloWorld, Params et OpenAPI
        if (path.startsWith("helloWorld") || path.startsWith("params") || path.startsWith("openapi")) {
            return true;
        }
        // GET /annonces et GET /annonces/{id} sont publics (lecture)
        if ("GET".equals(method) && path.startsWith("annonces")) {
            return true;
        }
        return false;
    }

    private void abortUnauthorized(ContainerRequestContext requestContext, String message) {
        ApiError error = new ApiError(401, "Unauthorized", message);
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .entity(error)
                        .type(MediaType.APPLICATION_JSON)
                        .build()
        );
    }
}
