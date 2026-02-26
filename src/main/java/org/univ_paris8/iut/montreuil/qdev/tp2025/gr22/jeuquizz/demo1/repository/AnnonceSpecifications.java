package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository;

import org.springframework.data.jpa.domain.Specification;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.AnnonceStatus;

import java.sql.Timestamp;

public final class AnnonceSpecifications {

    private AnnonceSpecifications() {
    }

    /**
     * Recherche par mot-clé dans le titre ou la description (LIKE %keyword%).
     */
    public static Specification<Annonce> keywordInTitleOrDescription(String keyword) {
        return (root, query, cb) -> {
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    /**
     * Filtre par statut.
     */
    public static Specification<Annonce> hasStatus(AnnonceStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    /**
     * Filtre par catégorie.
     */
    public static Specification<Annonce> hasCategoryId(Long categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    /**
     * Filtre par auteur.
     */
    public static Specification<Annonce> hasAuthorId(Long authorId) {
        return (root, query, cb) -> cb.equal(root.get("author").get("id"), authorId);
    }

    /**
     * Filtre par date de création >= fromDate.
     */
    public static Specification<Annonce> createdAfter(Timestamp fromDate) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), fromDate);
    }

    /**
     * Filtre par date de création <= toDate.
     */
    public static Specification<Annonce> createdBefore(Timestamp toDate) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), toDate);
    }
}
