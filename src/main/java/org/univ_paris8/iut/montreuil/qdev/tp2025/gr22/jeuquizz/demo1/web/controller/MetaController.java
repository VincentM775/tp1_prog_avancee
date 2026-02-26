package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meta")
@Tag(name = "Métadonnées", description = "Introspection des entités (champs filtrables et triables)")
public class MetaController {

    @Operation(
            summary = "Métadonnées de l'entité Annonce",
            description = "Retourne les champs, types, champs triables et filtrables de l'entité Annonce via réflexion Java"
    )
    @ApiResponse(responseCode = "200", description = "Métadonnées retournées")
    @SecurityRequirements
    @GetMapping("/annonces")
    public ResponseEntity<Map<String, Object>> annoncesMeta() {
        List<Map<String, String>> fields = new ArrayList<>();

        for (Field field : Annonce.class.getDeclaredFields()) {
            Map<String, String> fieldInfo = new LinkedHashMap<>();
            fieldInfo.put("name", field.getName());
            fieldInfo.put("type", field.getType().getSimpleName());
            fields.add(fieldInfo);
        }

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("entity", "Annonce");
        meta.put("fields", fields);
        meta.put("sortableFields", List.of("id", "title", "date", "status"));
        meta.put("filterableFields", List.of("q", "status", "categoryId", "authorId", "fromDate", "toDate"));

        return ResponseEntity.ok(meta);
    }
}
