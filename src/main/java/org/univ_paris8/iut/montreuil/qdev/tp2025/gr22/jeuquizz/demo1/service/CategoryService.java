package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service;

import jakarta.persistence.EntityManager;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.Category;

import java.util.List;
import java.util.Optional;

public class CategoryService extends AbstractService {

    public Category creer(String label) {
        EntityManager em = getEntityManager();
        try {
            return executeInTransaction(em, entityManager -> {
                if (existeParLabel(entityManager, label)) {
                    throw new BusinessException("La catégorie '" + label + "' existe déjà");
                }

                Category category = new Category(label);
                entityManager.persist(category);
                return category;
            });
        } finally {
            em.close();
        }
    }

    public Optional<Category> trouverParId(Long id) {
        EntityManager em = getEntityManager();
        try {
            return Optional.ofNullable(em.find(Category.class, id));
        } finally {
            em.close();
        }
    }

    public Optional<Category> trouverParLabel(String label) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT c FROM Category c WHERE c.label = :label", Category.class)
                .setParameter("label", label)
                .getResultStream()
                .findFirst();
        } finally {
            em.close();
        }
    }

    public List<Category> listerToutes() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT c FROM Category c ORDER BY c.label", Category.class)
                .getResultList();
        } finally {
            em.close();
        }
    }

    public Category modifier(Long id, String nouveauLabel) {
        EntityManager em = getEntityManager();
        try {
            return executeInTransaction(em, entityManager -> {
                Category category = entityManager.find(Category.class, id);
                if (category == null) {
                    throw new EntityNotFoundException("Catégorie", id);
                }

                if (!nouveauLabel.equals(category.getLabel())) {
                    if (existeParLabel(entityManager, nouveauLabel)) {
                        throw new BusinessException("La catégorie '" + nouveauLabel + "' existe déjà");
                    }
                    category.setLabel(nouveauLabel);
                }

                return category;
            });
        } finally {
            em.close();
        }
    }

    public void supprimer(Long id) {
        EntityManager em = getEntityManager();
        try {
            executeInTransaction(em, entityManager -> {
                Category category = entityManager.find(Category.class, id);
                if (category == null) {
                    throw new EntityNotFoundException("Catégorie", id);
                }

                Long count = entityManager.createQuery(
                    "SELECT COUNT(a) FROM Annonce a WHERE a.category.id = :categoryId", Long.class)
                    .setParameter("categoryId", id)
                    .getSingleResult();

                if (count > 0) {
                    throw new BusinessException(
                        "Impossible de supprimer la catégorie : " + count + " annonce(s) y sont associée(s)");
                }

                entityManager.remove(category);
            });
        } finally {
            em.close();
        }
    }

    public long compterAnnonces(Long categoryId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT COUNT(a) FROM Annonce a WHERE a.category.id = :categoryId", Long.class)
                .setParameter("categoryId", categoryId)
                .getSingleResult();
        } finally {
            em.close();
        }
    }

    private boolean existeParLabel(EntityManager em, String label) {
        Long count = em.createQuery(
            "SELECT COUNT(c) FROM Category c WHERE c.label = :label", Long.class)
            .setParameter("label", label)
            .getSingleResult();
        return count > 0;
    }
}
