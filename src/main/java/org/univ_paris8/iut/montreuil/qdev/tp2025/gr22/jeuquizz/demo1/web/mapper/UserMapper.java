package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.User;

import java.util.Collections;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "annonces", ignore = true)
    default Map<String, Object> toDto(User user) {
        if (user == null) return Collections.emptyMap();
        return Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "email", user.getEmail()
        );
    }
}
