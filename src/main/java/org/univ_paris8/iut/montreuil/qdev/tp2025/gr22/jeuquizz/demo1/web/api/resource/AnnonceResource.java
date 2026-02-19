package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.AnnonceService;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.EntityNotFoundException;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service.PagedResult;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.dto.AnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.dto.CreateAnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.api.dto.UpdateAnnonceDTO;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Path("/annonces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Annonces", description = "CRUD et gestion du cycle de vie des annonces")
public class AnnonceResource {

    private static final Logger log = LoggerFactory.getLogger(AnnonceResource.class);

    private AnnonceService annonceService;

    @Context
    private ContainerRequestContext requestContext;

    public AnnonceResource() {
        this.annonceService = new AnnonceService();
    }

    AnnonceResource(AnnonceService annonceService) {
        this.annonceService = annonceService;
    }

    private Long getCurrentUserId() {
        return (Long) requestContext.getProperty("userId");
    }

    @GET
    @Operation(summary = "Lister les annonces publiées", description = "Retourne une liste paginée des annonces au statut PUBLISHED")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste paginée")
    })
    public Response list(
            @Parameter(description = "Numéro de page (0-based)") @QueryParam("page") @DefaultValue("0") int page,
            @Parameter(description = "Nombre d'éléments par page") @QueryParam("size") @DefaultValue("10") int size) {

        log.debug("GET /annonces page={} size={}", page, size);

        PagedResult<Annonce> result = annonceService.listerPubliees(page, size);

        List<AnnonceDTO> dtos = result.getContent().stream()
                .map(AnnonceDTO::fromEntity)
                .toList();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", dtos);
        response.put("page", result.getPage());
        response.put("size", result.getSize());
        response.put("totalElements", result.getTotalElements());
        response.put("totalPages", result.getTotalPages());
        response.put("hasNext", result.hasNext());
        response.put("hasPrevious", result.hasPrevious());

        log.info("GET /annonces - {} résultats sur {} total", dtos.size(), result.getTotalElements());
        return Response.ok(response).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Détail d'une annonce")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Annonce trouvée"),
            @ApiResponse(responseCode = "404", description = "Annonce inexistante")
    })
    public Response getById(@Parameter(description = "ID de l'annonce") @PathParam("id") Long id) {
        log.debug("GET /annonces/{}", id);

        Annonce annonce = annonceService.trouverParId(id)
                .orElseThrow(() -> new EntityNotFoundException("Annonce", id));

        return Response.ok(AnnonceDTO.fromEntity(annonce)).build();
    }

    @POST
    @Operation(summary = "Créer une annonce", description = "L'auteur est l'utilisateur authentifié. Nécessite un token Bearer.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Annonce créée"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    public Response create(@Valid CreateAnnonceDTO dto) {
        Long currentUserId = getCurrentUserId();
        log.info("POST /annonces - userId={} title='{}'", currentUserId, dto.getTitle());

        Annonce annonce = dto.toEntity();
        Annonce created = annonceService.creer(annonce, currentUserId, dto.getCategoryId());

        log.info("Annonce créée id={} par userId={}", created.getId(), currentUserId);
        AnnonceDTO responseDto = AnnonceDTO.fromEntity(created);
        return Response.created(URI.create("/api/annonces/" + created.getId()))
                .entity(responseDto)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Modifier une annonce", description = "Seul l'auteur peut modifier. Annonce DRAFT uniquement.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Annonce mise à jour"),
            @ApiResponse(responseCode = "403", description = "Non auteur"),
            @ApiResponse(responseCode = "404", description = "Annonce inexistante"),
            @ApiResponse(responseCode = "409", description = "Conflit d'état (annonce publiée/archivée)")
    })
    public Response update(@PathParam("id") Long id, @Valid UpdateAnnonceDTO dto) {
        Long currentUserId = getCurrentUserId();
        log.info("PUT /annonces/{} - userId={}", id, currentUserId);

        Annonce updated = annonceService.modifier(
                id, dto.getTitle(), dto.getDescription(),
                dto.getAdress(), dto.getMail(), dto.getCategoryId(), currentUserId);

        return Response.ok(AnnonceDTO.fromEntity(updated)).build();
    }

    @PATCH
    @Path("/{id}")
    @Operation(summary = "Mise à jour partielle d'une annonce",
            description = "Met à jour uniquement les champs fournis (non null). Seul l'auteur peut modifier. Annonce DRAFT uniquement.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Annonce partiellement mise à jour"),
            @ApiResponse(responseCode = "403", description = "Non auteur"),
            @ApiResponse(responseCode = "404", description = "Annonce inexistante"),
            @ApiResponse(responseCode = "409", description = "Conflit d'état (annonce publiée/archivée)")
    })
    public Response patch(@PathParam("id") Long id, @Valid UpdateAnnonceDTO dto) {
        Long currentUserId = getCurrentUserId();
        log.info("PATCH /annonces/{} - userId={}", id, currentUserId);

        Annonce updated = annonceService.modifier(
                id, dto.getTitle(), dto.getDescription(),
                dto.getAdress(), dto.getMail(), dto.getCategoryId(), currentUserId);

        return Response.ok(AnnonceDTO.fromEntity(updated)).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Supprimer une annonce", description = "Seul l'auteur peut supprimer. L'annonce doit être ARCHIVED.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Annonce supprimée"),
            @ApiResponse(responseCode = "403", description = "Non auteur"),
            @ApiResponse(responseCode = "409", description = "Annonce non archivée")
    })
    public Response delete(@PathParam("id") Long id) {
        Long currentUserId = getCurrentUserId();
        log.info("DELETE /annonces/{} - userId={}", id, currentUserId);

        annonceService.supprimer(id, currentUserId);

        log.info("Annonce {} supprimée par userId={}", id, currentUserId);
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/publier")
    @Operation(summary = "Publier une annonce")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Annonce publiée"),
            @ApiResponse(responseCode = "409", description = "Conflit d'état")
    })
    public Response publier(@PathParam("id") Long id) {
        Long currentUserId = getCurrentUserId();
        log.info("POST /annonces/{}/publier - userId={}", id, currentUserId);

        Annonce published = annonceService.publier(id, currentUserId);
        return Response.ok(AnnonceDTO.fromEntity(published)).build();
    }

    @POST
    @Path("/{id}/archiver")
    @Operation(summary = "Archiver une annonce")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Annonce archivée"),
            @ApiResponse(responseCode = "409", description = "Conflit d'état")
    })
    public Response archiver(@PathParam("id") Long id) {
        Long currentUserId = getCurrentUserId();
        log.info("POST /annonces/{}/archiver - userId={}", id, currentUserId);

        Annonce archived = annonceService.archiver(id, currentUserId);
        return Response.ok(AnnonceDTO.fromEntity(archived)).build();
    }
}
