package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.dao;

import java.sql.Connection;
import java.util.List;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.database.ConnectionDB;

public abstract class DAO<T> {
    public Connection connect = null;

    public DAO() {
        try {
            this.connect = ConnectionDB.getInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode de création
     * @param obj
     * @return T
     */
    public abstract T create(T obj);

    /**
     * Méthode de recherche
     * @param id
     * @return T
     */
    public abstract T find(long id);

    /**
     * Méthode de mise à jour
     * @param obj
     * @return T
     */
    public abstract T update(T obj);

    /**
     * Méthode de suppression
     * @param obj
     */
    public abstract void delete(T obj);

    /**
     * Méthode pour récupérer tous les objets
     * @return List<T>
     */
    public abstract List<T> findAll();
}