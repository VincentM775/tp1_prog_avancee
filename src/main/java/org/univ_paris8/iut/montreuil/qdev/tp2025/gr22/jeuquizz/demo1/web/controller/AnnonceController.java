package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.AnnonceService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.dto.AnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.dto.ApiError;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.dto.CreateAnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.dto.UpdateAnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.mapper.AnnonceMapper;

import java.net.URI;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/annonces")
@Tag(name = "Annonces", description = "CRUD et gestion du cycle de vie des annonces")
public class AnnonceController {

    private final AnnonceService annonceService;
    private final AnnonceMapper annonceMapper;

    public AnnonceController(AnnonceService annonceService, AnnonceMapper annonceMapper) {
        this.annonceService = annonceService;
        this.annonceMapper = annonceMapper;
    }

    @Operation(summary = "Rechercher des annonces", description = "Recherche multi-critères avec pagination et tri")
    @ApiResponse(responseCode = "200", description = "Liste paginée d'annonces")
    @ApiResponse(responseCode = "401", description = "Non authentifié",
            content = @Content(schema = @Schema(hidden = true)))
    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @Parameter(description = "Mot-clé dans le titre ou la description") @RequestParam(required = false) String q,
            @Parameter(description = "Statut (DRAFT, PUBLISHED, ARCHIVED)") @RequestParam(required = false) String status,
            @Parameter(description = "ID de la catégorie") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "ID de l'auteur") @RequestParam(required = false) Long authorId,
            @Parameter(description = "Date de début (yyyy-MM-dd)") @RequestParam(required = false) String fromDate,
            @Parameter(description = "Date de fin (yyyy-MM-dd)") @RequestParam(required = false) String toDate,
            @PageableDefault(size = 10, sort = "date") Pageable pageable) {

        AnnonceStatus annonceStatus = null;
        if (status != null && !status.isBlank()) {
            annonceStatus = AnnonceStatus.valueOf(status.toUpperCase());
        }

        Timestamp fromTimestamp = (fromDate != null && !fromDate.isBlank())
                ? Timestamp.valueOf(fromDate + " 00:00:00") : null;
        Timestamp toTimestamp = (toDate != null && !toDate.isBlank())
                ? Timestamp.valueOf(toDate + " 23:59:59") : null;

        Page<Annonce> page = annonceService.rechercher(
                q, annonceStatus, categoryId, authorId, fromTimestamp, toTimestamp, pageable);

        List<AnnonceDTO> dtos = page.getContent().stream()
                .map(annonceMapper::toDto)
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", dtos);
        response.put("page", page.getNumber());
        response.put("size", page.getSize());
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("hasNext", page.hasNext());
        response.put("hasPrevious", page.hasPrevious());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Détail d'une annonce", description = "Retourne une annonce par son identifiant")
    @ApiResponse(responseCode = "200", description = "Annonce trouvée",
            content = @Content(schema = @Schema(implementation = AnnonceDTO.class)))
    @ApiResponse(responseCode = "404", description = "Annonce inexistante",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "Non authentifié",
            content = @Content(schema = @Schema(hidden = true)))
    @GetMapping("/{id}")
    public ResponseEntity<AnnonceDTO> getById(@PathVariable Long id) {
        Annonce annonce = annonceService.trouverParId(id);
        return ResponseEntity.ok(annonceMapper.toDto(annonce));
    }

    @Operation(summary = "Créer une annonce", description = "Crée une nouvelle annonce avec le statut DRAFT")
    @ApiResponse(responseCode = "201", description = "Annonce créée",
            content = @Content(schema = @Schema(implementation = AnnonceDTO.class)))
    @ApiResponse(responseCode = "400", description = "Données invalides",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "Non authentifié",
            content = @Content(schema = @Schema(hidden = true)))
    @PostMapping
    public ResponseEntity<AnnonceDTO> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Données de la nouvelle annonce",
                    content = @Content(examples = @ExampleObject(
                            name = "Exemple de création",
                            value = """
                                    {
                                        "title": "Appartement T3",
                                        "description": "Bel appartement lumineux centre-ville",
                                        "adress": "Paris 11e",
                                        "mail": "contact@exemple.com",
                                        "categoryId": 1
                                    }
                                    """
                    ))
            )
            @Valid @RequestBody CreateAnnonceDTO dto,
            @Parameter(hidden = true) @RequestAttribute(value = "userId", required = false) Long currentUserId) {

        Annonce annonce = annonceMapper.toEntity(dto);
        Annonce created = annonceService.creer(annonce, currentUserId, dto.getCategoryId());

        AnnonceDTO responseDto = annonceMapper.toDto(created);
        return ResponseEntity
                .created(URI.create("/api/annonces/" + created.getId()))
                .body(responseDto);
    }

    @Operation(summary = "Modifier une annonce", description = "Modifie une annonce existante (DRAFT uniquement, par l'auteur)")
    @ApiResponse(responseCode = "200", description = "Annonce modifiée",
            content = @Content(schema = @Schema(implementation = AnnonceDTO.class)))
    @ApiResponse(responseCode = "403", description = "Non autorisé (pas l'auteur)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Annonce inexistante",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "Annonce non modifiable (PUBLISHED ou ARCHIVED)",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "Non authentifié",
            content = @Content(schema = @Schema(hidden = true)))
    @PutMapping("/{id}")
    public ResponseEntity<AnnonceDTO> update(
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Champs à modifier",
                    content = @Content(examples = @ExampleObject(
                            name = "Exemple de modification",
                            value = """
                                    {
                                        "title": "Titre modifié",
                                        "description": "Nouvelle description"
                                    }
                                    """
                    ))
            )
            @Valid @RequestBody UpdateAnnonceDTO dto,
            @Parameter(hidden = true) @RequestAttribute(value = "userId", required = false) Long currentUserId) {

        return applyUpdate(id, dto, currentUserId);
    }

    @Operation(summary = "Modifier partiellement une annonce", description = "Mise à jour partielle (PATCH) - mêmes règles que PUT mais sémantiquement pour des modifications partielles")
    @ApiResponse(responseCode = "200", description = "Annonce modifiée")
    @ApiResponse(responseCode = "403", description = "Non autorisé",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Annonce inexistante",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "Annonce non modifiable",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PatchMapping("/{id}")
    public ResponseEntity<AnnonceDTO> patch(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAnnonceDTO dto,
            @Parameter(hidden = true) @RequestAttribute(value = "userId", required = false) Long currentUserId) {

        return applyUpdate(id, dto, currentUserId);
    }

    @Operation(summary = "Supprimer une annonce", description = "Supprime une annonce ARCHIVED uniquement")
    @ApiResponse(responseCode = "204", description = "Annonce supprimée")
    @ApiResponse(responseCode = "403", description = "Non autorisé",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Annonce inexistante",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "Annonce non archivée",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "Non authentifié",
            content = @Content(schema = @Schema(hidden = true)))
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestAttribute(value = "userId", required = false) Long currentUserId) {

        annonceService.supprimer(id, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Publier une annonce", description = "Change le statut d'une annonce DRAFT vers PUBLISHED")
    @ApiResponse(responseCode = "200", description = "Annonce publiée",
            content = @Content(schema = @Schema(implementation = AnnonceDTO.class)))
    @ApiResponse(responseCode = "403", description = "Non autorisé",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Annonce inexistante",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "Annonce déjà publiée ou archivée",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PostMapping("/{id}/publier")
    public ResponseEntity<AnnonceDTO> publier(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestAttribute(value = "userId", required = false) Long currentUserId) {

        Annonce published = annonceService.publier(id, currentUserId);
        return ResponseEntity.ok(annonceMapper.toDto(published));
    }

    @Operation(summary = "Archiver une annonce", description = "Change le statut vers ARCHIVED (ADMIN uniquement)")
    @ApiResponse(responseCode = "200", description = "Annonce archivée",
            content = @Content(schema = @Schema(implementation = AnnonceDTO.class)))
    @ApiResponse(responseCode = "403", description = "Rôle ADMIN requis",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Annonce inexistante",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "Annonce déjà archivée",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @PostMapping("/{id}/archiver")
    public ResponseEntity<AnnonceDTO> archiver(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestAttribute(value = "userId", required = false) Long currentUserId) {

        Annonce archived = annonceService.archiver(id, currentUserId);
        return ResponseEntity.ok(annonceMapper.toDto(archived));
    }

    private ResponseEntity<AnnonceDTO> applyUpdate(Long id, UpdateAnnonceDTO dto, Long currentUserId) {
        Annonce updated = annonceService.modifier(id, dto, currentUserId);
        return ResponseEntity.ok(annonceMapper.toDto(updated));
    }
}
