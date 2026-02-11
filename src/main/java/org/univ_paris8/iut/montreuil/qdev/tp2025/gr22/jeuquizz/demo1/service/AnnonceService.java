package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Annonce;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.AnnonceStatus;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Category;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.User;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AnnonceService extends AbstractService {

    public Annonce creer(Annonce annonce, Long authorId, Long categoryId) {
        EntityManager em = getEntityManager();
        try {
            return executeInTransaction(em, entityManager -> {
                User author = entityManager.find(User.class, authorId);
                if (author == null) {
                    throw new EntityNotFoundException("Utilisateur", authorId);
                }

                Category category = null;
                if (categoryId != null) {
                    category = entityManager.find(Category.class, categoryId);
                    if (category == null) {
                        throw new EntityNotFoundException("Catégorie", categoryId);
                    }
                }

                annonce.setAuthor(author);
                annonce.setCategory(category);
                annonce.setStatus(AnnonceStatus.DRAFT);
                annonce.setDate(new Timestamp(System.currentTimeMillis()));

                entityManager.persist(annonce);
                return annonce;
            });
        } finally {
            em.close();
        }
    }

    public Annonce modifier(Long id, String title, String description,
                           String adress, String mail, Long categoryId) {
        EntityManager em = getEntityManager();
        try {
            return executeInTransaction(em, entityManager -> {
                Annonce annonce = entityManager.find(Annonce.class, id);
                if (annonce == null) {
                    throw new EntityNotFoundException("Annonce", id);
                }

                if (annonce.getStatus() == AnnonceStatus.ARCHIVED) {
                    throw new BusinessException("Impossible de modifier une annonce archivée");
                }

                if (title != null) {
                    annonce.setTitle(title);
                }
                if (description != null) {
                    annonce.setDescription(description);
                }
                if (adress != null) {
                    annonce.setAdress(adress);
                }
                if (mail != null) {
                    annonce.setMail(mail);
                }

                if (categoryId != null) {
                    Category category = entityManager.find(Category.class, categoryId);
                    if (category == null) {
                        throw new EntityNotFoundException("Catégorie", categoryId);
                    }
                    annonce.setCategory(category);
                }

                return annonce;
            });
        } finally {
            em.close();
        }
    }

    public Annonce publier(Long id) {
        EntityManager em = getEntityManager();
        try {
            return executeInTransaction(em, entityManager -> {
                Annonce annonce = entityManager.find(Annonce.class, id);
                if (annonce == null) {
                    throw new EntityNotFoundException("Annonce", id);
                }

                if (annonce.getStatus() == AnnonceStatus.PUBLISHED) {
                    throw new BusinessException("L'annonce est déjà publiée");
                }
                if (annonce.getStatus() == AnnonceStatus.ARCHIVED) {
                    throw new BusinessException("Impossible de publier une annonce archivée");
                }

                annonce.setStatus(AnnonceStatus.PUBLISHED);
                return annonce;
            });
        } finally {
            em.close();
        }
    }

    public Annonce archiver(Long id) {
        EntityManager em = getEntityManager();
        try {
            return executeInTransaction(em, entityManager -> {
                Annonce annonce = entityManager.find(Annonce.class, id);
                if (annonce == null) {
                    throw new EntityNotFoundException("Annonce", id);
                }

                if (annonce.getStatus() == AnnonceStatus.ARCHIVED) {
                    throw new BusinessException("L'annonce est déjà archivée");
                }

                annonce.setStatus(AnnonceStatus.ARCHIVED);
                return annonce;
            });
        } finally {
            em.close();
        }
    }

    public void supprimer(Long id) {
        EntityManager em = getEntityManager();
        try {
            executeInTransaction(em, entityManager -> {
                Annonce annonce = entityManager.find(Annonce.class, id);
                if (annonce == null) {
                    throw new EntityNotFoundException("Annonce", id);
                }

                entityManager.remove(annonce);
            });
        } finally {
            em.close();
        }
    }

    public Optional<Annonce> trouverParId(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT a FROM Annonce a " +
                "LEFT JOIN FETCH a.category " +
                "LEFT JOIN FETCH a.author " +
                "WHERE a.id = :id",
                Annonce.class
            ).setParameter("id", id)
             .getResultStream()
             .findFirst();
        } finally {
            em.close();
        }
    }

    public PagedResult<Annonce> listerPubliees(int page, int size) {
        EntityManager em = getEntityManager();
        try {
            Long total = em.createQuery(
                "SELECT COUNT(a) FROM Annonce a WHERE a.status = :status",
                Long.class
            ).setParameter("status", AnnonceStatus.PUBLISHED)
             .getSingleResult();

            List<Annonce> annonces = em.createQuery(
                "SELECT a FROM Annonce a " +
                "LEFT JOIN FETCH a.category " +
                "LEFT JOIN FETCH a.author " +
                "WHERE a.status = :status ORDER BY a.date DESC",
                Annonce.class
            ).setParameter("status", AnnonceStatus.PUBLISHED)
             .setFirstResult(page * size)
             .setMaxResults(size)
             .getResultList();

            return new PagedResult<>(annonces, page, size, total);
        } finally {
            em.close();
        }
    }

    public PagedResult<Annonce> listerParAuteur(Long authorId, int page, int size) {
        EntityManager em = getEntityManager();
        try {
            Long total = em.createQuery(
                "SELECT COUNT(a) FROM Annonce a WHERE a.author.id = :authorId",
                Long.class
            ).setParameter("authorId", authorId)
             .getSingleResult();

            List<Annonce> annonces = em.createQuery(
                "SELECT a FROM Annonce a " +
                "LEFT JOIN FETCH a.category " +
                "LEFT JOIN FETCH a.author " +
                "WHERE a.author.id = :authorId ORDER BY a.date DESC",
                Annonce.class
            ).setParameter("authorId", authorId)
             .setFirstResult(page * size)
             .setMaxResults(size)
             .getResultList();

            return new PagedResult<>(annonces, page, size, total);
        } finally {
            em.close();
        }
    }

    public PagedResult<Annonce> rechercher(String keyword, Long categoryId,
                                           AnnonceStatus status, int page, int size) {
        EntityManager em = getEntityManager();
        try {
            StringBuilder whereClause = new StringBuilder("WHERE 1=1");
            Map<String, Object> params = new HashMap<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                whereClause.append(" AND (LOWER(a.title) LIKE LOWER(:keyword)")
                          .append(" OR LOWER(a.description) LIKE LOWER(:keyword))");
                params.put("keyword", "%" + keyword + "%");
            }

            if (categoryId != null) {
                whereClause.append(" AND a.category.id = :categoryId");
                params.put("categoryId", categoryId);
            }

            if (status != null) {
                whereClause.append(" AND a.status = :status");
                params.put("status", status);
            }

            String countJpql = "SELECT COUNT(a) FROM Annonce a " + whereClause;
            TypedQuery<Long> countQuery = em.createQuery(countJpql, Long.class);
            params.forEach(countQuery::setParameter);
            Long total = countQuery.getSingleResult();

            String selectJpql = "SELECT a FROM Annonce a " +
                "LEFT JOIN FETCH a.category " +
                "LEFT JOIN FETCH a.author " +
                whereClause + " ORDER BY a.date DESC";
            TypedQuery<Annonce> selectQuery = em.createQuery(selectJpql, Annonce.class);
            params.forEach(selectQuery::setParameter);
            selectQuery.setFirstResult(page * size);
            selectQuery.setMaxResults(size);
            List<Annonce> annonces = selectQuery.getResultList();

            return new PagedResult<>(annonces, page, size, total);
        } finally {
            em.close();
        }
    }

    public long compterParStatut(AnnonceStatus status) {
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
