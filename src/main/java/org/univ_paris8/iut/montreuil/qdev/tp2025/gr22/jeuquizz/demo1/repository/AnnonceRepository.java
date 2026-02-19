package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.AnnonceStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnonceRepository extends AbstractRepository<Annonce, Long> {

    public AnnonceRepository() {
        super(Annonce.class);
    }

    public List<Annonce> findByKeyword(String keyword) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Annonce> query = em.createQuery(
                "SELECT a FROM Annonce a " +
                "WHERE LOWER(a.title) LIKE LOWER(:keyword) " +
                "   OR LOWER(a.description) LIKE LOWER(:keyword) " +
                "ORDER BY a.date DESC",
                Annonce.class
            );
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> findByKeyword(String keyword, int page, int size) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Annonce> query = em.createQuery(
                "SELECT a FROM Annonce a " +
                "WHERE LOWER(a.title) LIKE LOWER(:keyword) " +
                "   OR LOWER(a.description) LIKE LOWER(:keyword) " +
                "ORDER BY a.date DESC",
                Annonce.class
            );
            query.setParameter("keyword", "%" + keyword + "%");
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> findByCategoryId(Long categoryId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Annonce> query = em.createQuery(
                "SELECT a FROM Annonce a WHERE a.category.id = :categoryId ORDER BY a.date DESC",
                Annonce.class
            );
            query.setParameter("categoryId", categoryId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> findByCategoryId(Long categoryId, int page, int size) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Annonce> query = em.createQuery(
                "SELECT a FROM Annonce a WHERE a.category.id = :categoryId ORDER BY a.date DESC",
                Annonce.class
            );
            query.setParameter("categoryId", categoryId);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> findByStatus(AnnonceStatus status) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Annonce> query = em.createQuery(
                "SELECT a FROM Annonce a WHERE a.status = :status ORDER BY a.date DESC",
                Annonce.class
            );
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> findPublished() {
        return findByStatus(AnnonceStatus.PUBLISHED);
    }

    public List<Annonce> findPublished(int page, int size) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Annonce> query = em.createQuery(
                "SELECT a FROM Annonce a WHERE a.status = :status ORDER BY a.date DESC, a.id DESC",
                Annonce.class
            );
            query.setParameter("status", AnnonceStatus.PUBLISHED);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> findByAuthorId(Long authorId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Annonce> query = em.createQuery(
                "SELECT a FROM Annonce a WHERE a.author.id = :authorId ORDER BY a.date DESC",
                Annonce.class
            );
            query.setParameter("authorId", authorId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Annonce> search(String keyword, Long categoryId, AnnonceStatus status, int page, int size) {
        EntityManager em = getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT a FROM Annonce a WHERE 1=1");
            Map<String, Object> params = new HashMap<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                jpql.append(" AND (LOWER(a.title) LIKE LOWER(:keyword) OR LOWER(a.description) LIKE LOWER(:keyword))");
                params.put("keyword", "%" + keyword + "%");
            }

            if (categoryId != null) {
                jpql.append(" AND a.category.id = :categoryId");
                params.put("categoryId", categoryId);
            }

            if (status != null) {
                jpql.append(" AND a.status = :status");
                params.put("status", status);
            }

            jpql.append(" ORDER BY a.date DESC");

            TypedQuery<Annonce> query = em.createQuery(jpql.toString(), Annonce.class);

            for (Map.Entry<String, Object> param : params.entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }

            query.setFirstResult(page * size);
            query.setMaxResults(size);

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public long countSearch(String keyword, Long categoryId, AnnonceStatus status) {
        EntityManager em = getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT COUNT(a) FROM Annonce a WHERE 1=1");
            Map<String, Object> params = new HashMap<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                jpql.append(" AND (LOWER(a.title) LIKE LOWER(:keyword) OR LOWER(a.description) LIKE LOWER(:keyword))");
                params.put("keyword", "%" + keyword + "%");
            }

            if (categoryId != null) {
                jpql.append(" AND a.category.id = :categoryId");
                params.put("categoryId", categoryId);
            }

            if (status != null) {
                jpql.append(" AND a.status = :status");
                params.put("status", status);
            }

            TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);

            for (Map.Entry<String, Object> param : params.entrySet()) {
                query.setParameter(param.getKey(), param.getValue());
            }

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    public long countByStatus(AnnonceStatus status) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT COUNT(a) FROM Annonce a WHERE a.status = :status",
                Long.class
            ).setParameter("status", status)
             .getSingleResult();
        } finally {
            em.close();
        }
    }
}
