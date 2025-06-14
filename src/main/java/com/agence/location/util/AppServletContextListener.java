package com.agence.location.util;

import com.agence.location.dao.JPAUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Listener d'application qui gère le cycle de vie de l'EntityManagerFactory.
 * Il initialise l'EMF au démarrage du contexte web et la ferme à l'arrêt.
 */
@WebListener // Annotation pour enregistrer ce listener automatiquement
public class AppServletContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Cette méthode est appelée au démarrage du contexte web.
        // C'est l'endroit idéal pour initialiser des ressources coûteuses comme l'EntityManagerFactory.
        // L'EMF est déjà initialisée dans le bloc static de JPAUtil,
        // donc un simple appel à une méthode de JPAUtil ou l'accès à son getter
        // suffira à s'assurer qu'elle est chargée.
        System.out.println("Application démarrée. Initialisation des ressources JPA...");
        // JPAUtil.getEntityManager(); // Appel optionnel pour s'assurer que la classe est chargée
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cette méthode est appelée à l'arrêt du contexte web.
        // C'est l'endroit pour libérer les ressources.
        System.out.println("Application arrêtée. Fermeture de l'EntityManagerFactory...");
        JPAUtil.closeEntityManagerFactory();
    }
}
