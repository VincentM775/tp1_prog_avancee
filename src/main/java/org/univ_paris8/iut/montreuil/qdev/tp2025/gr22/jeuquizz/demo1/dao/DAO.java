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

    public abstract T create(T obj);

    public abstract T find(long id);

    public abstract T update(T obj);

    public abstract void delete(T obj);

    public abstract List<T> findAll();
}
