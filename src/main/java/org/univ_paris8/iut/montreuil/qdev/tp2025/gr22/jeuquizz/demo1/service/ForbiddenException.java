package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service;

/**
 * Exception levée lorsqu'un utilisateur authentifié n'a pas les droits
 * pour effectuer l'action demandée (ex: modifier l'annonce d'un autre auteur).
 * Mappée en HTTP 403 Forbidden.
 */
public class ForbiddenException extends ServiceException {

    public ForbiddenException(String message) {
        super(message);
    }
}
