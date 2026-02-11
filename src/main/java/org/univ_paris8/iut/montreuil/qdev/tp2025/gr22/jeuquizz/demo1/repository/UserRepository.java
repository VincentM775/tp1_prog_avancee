package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.User;

import java.util.List;
import java.util.Optional;

public class UserRepository extends AbstractRepository<User, Long> {

    public UserRepository() {
        super(User.class);
    }

    public Optional<User> findByUsername(String username) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username",
                User.class
            );
            query.setParameter("username", username);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public Optional<User> findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email",
                User.class
            );
            query.setParameter("email", email);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    public List<User> findByUsernameContaining(String keyword) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(:keyword)",
                User.class
            );
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public boolean existsByUsername(String username) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.username = :username",
                Long.class
            ).setParameter("username", username)
             .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    public boolean existsByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.email = :email",
                Long.class
            ).setParameter("email", email)
             .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}
