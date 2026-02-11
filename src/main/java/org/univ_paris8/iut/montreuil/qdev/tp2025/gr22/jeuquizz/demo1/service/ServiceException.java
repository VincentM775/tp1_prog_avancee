package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.service;

public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
