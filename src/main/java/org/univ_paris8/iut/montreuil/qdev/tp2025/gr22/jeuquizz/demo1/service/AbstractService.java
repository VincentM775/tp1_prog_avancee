package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service;

import jakarta.persistence.EntityManager;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.persistence.EntityManagerUtil;

public abstract class AbstractService {

    protected EntityManager getEntityManager() {
        return EntityManagerUtil.getEntityManager();
    }

    protected <T> T executeInTransaction(EntityManager em, TransactionalOperation<T> operation) {
        try {
            em.getTransaction().begin();
            T result = operation.execute(em);
            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    protected void executeInTransaction(EntityManager em, TransactionalVoidOperation operation) {
        try {
            em.getTransaction().begin();
            operation.execute(em);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    @FunctionalInterface
    protected interface TransactionalOperation<T> {
        T execute(EntityManager em);
    }

    @FunctionalInterface
    protected interface TransactionalVoidOperation {
        void execute(EntityManager em);
    }
}
