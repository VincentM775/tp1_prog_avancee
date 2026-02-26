package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.mapper;

import org.mapstruct.*;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.dto.AnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.dto.CreateAnnonceDTO;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.dto.UpdateAnnonceDTO;

@Mapper(componentModel = "spring")
public interface AnnonceMapper {

    @Mapping(source = "date", target = "dateCreation")
    @Mapping(source = "author.username", target = "authorUsername")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "category.label", target = "categoryLabel")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "status", target = "status")
    AnnonceDTO toDto(Annonce annonce);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    Annonce toEntity(CreateAnnonceDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntityFromDto(UpdateAnnonceDTO dto, @MappingTarget Annonce annonce);

    default String statusToString(org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.AnnonceStatus status) {
        return status != null ? status.name() : null;
    }
}
