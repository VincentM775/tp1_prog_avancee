package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Category;

import java.util.List;
import java.util.Optional;

public class CategoryRepository extends AbstractRepository<Category, Long> {

    public CategoryRepository() {
        super(Category.class);
    }

    public Optional<Category> findByLabel(String label) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Category> query = em.createQuery(
                "SELECT c FROM Category c WHERE c.label = :label",
                Category.class
            );
            query.setParameter("label", label);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public List<Category> findByLabelContaining(String keyword) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Category> query = em.createQuery(
                "SELECT c FROM Category c WHERE LOWER(c.label) LIKE LOWER(:keyword) ORDER BY c.label",
                Category.class
            );
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Category> findAllOrderByLabel() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Category> query = em.createQuery(
                "SELECT c FROM Category c ORDER BY c.label ASC",
                Category.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public boolean existsByLabel(String label) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(c) FROM Category c WHERE c.label = :label",
                Long.class
            ).setParameter("label", label)
             .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    public long countAnnoncesByCategoryId(Long categoryId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT COUNT(a) FROM Annonce a WHERE a.category.id = :categoryId",
                Long.class
            ).setParameter("categoryId", categoryId)
             .getSingleResult();
        } finally {
            em.close();
        }
    }
}
