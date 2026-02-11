package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.persistence.EntityManagerUtil;

import java.util.List;
import java.util.Optional;

public abstract class AbstractRepository<T, ID> implements GenericRepository<T, ID> {

    protected final Class<T> entityClass;

    protected AbstractRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected EntityManager getEntityManager() {
        return EntityManagerUtil.getEntityManager();
    }

    @Override
    public T save(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T savedEntity = em.merge(entity);
            em.getTransaction().commit();
            return savedEntity;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        EntityManager em = getEntityManager();
        try {
            T entity = em.find(entityClass, id);
            return Optional.ofNullable(entity);
        } finally {
            em.close();
        }
    }

    @Override
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(T entity) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T managedEntity = em.merge(entity);
            em.remove(managedEntity);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(ID id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public long count() {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e";
            return em.createQuery(jpql, Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    public List<T> findAll(int page, int size) {
        EntityManager em = getEntityManager();
        try {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            query.setFirstResult(page * size);
            query.setMaxResults(size);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
