package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service;

public class EntityNotFoundException extends ServiceException {

    public EntityNotFoundException(String entityName, Long id) {
        super(entityName + " avec l'ID " + id + " non trouvé(e)");
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
