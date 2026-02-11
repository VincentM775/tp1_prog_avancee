package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service;

import jakarta.persistence.EntityManager;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.entity.User;

import java.util.Optional;

public class UserService extends AbstractService {

    public User creer(String username, String email, String password) {
        EntityManager em = getEntityManager();
        try {
            return executeInTransaction(em, entityManager -> {
                if (existeParUsername(entityManager, username)) {
                    throw new BusinessException("Le nom d'utilisateur '" + username + "' est déjà utilisé");
                }

                if (existeParEmail(entityManager, email)) {
                    throw new BusinessException("L'email '" + email + "' est déjà utilisé");
                }

                User user = new User(username, email, password);
                entityManager.persist(user);
                return user;
            });
        } finally {
            em.close();
        }
    }

    public Optional<User> trouverParId(Long id) {
        EntityManager em = getEntityManager();
        try {
            return Optional.ofNullable(em.find(User.class, id));
        } finally {
            em.close();
        }
    }

    public Optional<User> trouverParUsername(String username) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
        } finally {
            em.close();
        }
    }

    public Optional<User> trouverParEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
        } finally {
            em.close();
        }
    }

    public User modifier(Long id, String username, String email) {
        EntityManager em = getEntityManager();
        try {
            return executeInTransaction(em, entityManager -> {
                User user = entityManager.find(User.class, id);
                if (user == null) {
                    throw new EntityNotFoundException("Utilisateur", id);
                }

                if (username != null && !username.equals(user.getUsername())) {
                    if (existeParUsername(entityManager, username)) {
                        throw new BusinessException("Le nom d'utilisateur '" + username + "' est déjà utilisé");
                    }
                    user.setUsername(username);
                }

                if (email != null && !email.equals(user.getEmail())) {
                    if (existeParEmail(entityManager, email)) {
                        throw new BusinessException("L'email '" + email + "' est déjà utilisé");
                    }
                    user.setEmail(email);
                }

                return user;
            });
        } finally {
            em.close();
        }
    }

    public void supprimer(Long id) {
        EntityManager em = getEntityManager();
        try {
            executeInTransaction(em, entityManager -> {
                User user = entityManager.find(User.class, id);
                if (user == null) {
                    throw new EntityNotFoundException("Utilisateur", id);
                }
                entityManager.remove(user);
            });
        } finally {
            em.close();
        }
    }

    private boolean existeParUsername(EntityManager em, String username) {
        Long count = em.createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
            .setParameter("username", username)
            .getSingleResult();
        return count > 0;
    }

    private boolean existeParEmail(EntityManager em, String email) {
        Long count = em.createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
            .setParameter("email", email)
            .getSingleResult();
        return count > 0;
    }
}
