package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.mapper;

import org.mapstruct.Mapper;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Category;

import java.util.Collections;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    default Map<String, Object> toDto(Category category) {
        if (category == null) return Collections.emptyMap();
        return Map.of(
            "id", category.getId(),
            "label", category.getLabel()
        );
    }
}
