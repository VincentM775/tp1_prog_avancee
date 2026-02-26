package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.User;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository.UserRepository;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.security.JwtService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.BusinessException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.dto.ApiError;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.dto.LoginRequest;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.dto.LoginResponse;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Endpoints d'authentification (login, token JWT)")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Operation(
            summary = "Connexion utilisateur",
            description = "Authentifie un utilisateur et retourne un token JWT à utiliser dans le header Authorization"
    )
    @ApiResponse(responseCode = "200", description = "Authentification réussie",
            content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    @ApiResponse(responseCode = "400", description = "Identifiants invalides ou champs manquants",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @SecurityRequirements
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Identifiants de connexion",
                    content = @Content(examples = @ExampleObject(
                            name = "Exemple de login",
                            value = """
                                    {
                                        "username": "user1",
                                        "password": "password123"
                                    }
                                    """
                    ))
            )
            @Valid @RequestBody LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("Identifiants invalides"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Identifiants invalides");
        }

        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole());

        return ResponseEntity.ok(new LoginResponse(token));
    }
}
