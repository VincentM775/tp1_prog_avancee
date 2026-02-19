package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service;

/**
 * Exception levée lors d'un conflit métier (ex: état incohérent, doublon).
 * Mappée en HTTP 409 Conflict.
 */
public class ConflictException extends ServiceException {

    public ConflictException(String message) {
        super(message);
    }
}
