package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.AnnonceStatus;

import java.util.Optional;

public interface AnnonceRepository extends JpaRepository<Annonce, Long>, JpaSpecificationExecutor<Annonce> {

    Page<Annonce> findByStatus(AnnonceStatus status, Pageable pageable);

    Page<Annonce> findByAuthorId(Long authorId, Pageable pageable);

    @Query("SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category WHERE a.id = :id")
    Optional<Annonce> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT a FROM Annonce a LEFT JOIN FETCH a.author LEFT JOIN FETCH a.category WHERE a.status = :status")
    Page<Annonce> findByStatusWithRelations(@Param("status") AnnonceStatus status, Pageable pageable);

    long countByStatus(AnnonceStatus status);
}
