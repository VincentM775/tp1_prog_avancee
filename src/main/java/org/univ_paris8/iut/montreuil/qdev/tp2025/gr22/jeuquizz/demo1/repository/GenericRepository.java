package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.repository;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void delete(T entity);

    void deleteById(ID id);

    long count();
}
