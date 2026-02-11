package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.persistence;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class PersistenceListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Initialisation de JPA/Hibernate...");
        EntityManagerUtil.getEntityManagerFactory();
        System.out.println("JPA/Hibernate initialisé avec succès.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Fermeture de l'EntityManagerFactory...");
        EntityManagerUtil.close();
        System.out.println("EntityManagerFactory fermée.");
    }
}
