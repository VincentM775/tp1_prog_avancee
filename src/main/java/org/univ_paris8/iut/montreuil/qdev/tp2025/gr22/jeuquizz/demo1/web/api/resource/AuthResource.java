package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.BusinessException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.TokenService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.UserService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.dto.LoginDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.dto.LoginResponseDTO;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentification", description = "Login / Logout stateless par token")
public class AuthResource {

    private static final Logger log = LoggerFactory.getLogger(AuthResource.class);

    private final UserService userService = new UserService();
    private final TokenService tokenService = TokenService.getInstance();

    @POST
    @Path("/login")
    @Operation(summary = "Authentification", description = "Retourne un token Bearer à utiliser dans le header Authorization")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentification réussie, token retourné"),
            @ApiResponse(responseCode = "400", description = "Identifiants invalides")
    })
    public Response login(@Valid LoginDTO dto) {
        log.info("Tentative de login pour username='{}'", dto.getUsername());

        User user = userService.trouverParUsername(dto.getUsername())
                .orElseThrow(() -> new BusinessException("Identifiants invalides"));

        if (!user.getPassword().equals(dto.getPassword())) {
            log.warn("Échec login pour username='{}' - mot de passe incorrect", dto.getUsername());
            throw new BusinessException("Identifiants invalides");
        }

        String token = tokenService.generateToken(user.getId());
        log.info("Login réussi pour userId={} username='{}'", user.getId(), user.getUsername());

        LoginResponseDTO response = new LoginResponseDTO(token, user.getId(), user.getUsername());
        return Response.ok(response).build();
    }

    @POST
    @Path("/logout")
    @Operation(summary = "Déconnexion", description = "Révoque le token")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Token révoqué")
    })
    public Response logout(@jakarta.ws.rs.HeaderParam("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenService.revokeToken(token);
            log.info("Token révoqué (logout)");
        }
        return Response.noContent().build();
    }
}
