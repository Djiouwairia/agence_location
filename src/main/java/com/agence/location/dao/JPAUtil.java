package com.agence.location.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Classe utilitaire pour la gestion de l'EntityManagerFactory et de l'EntityManager.
 * Assure que l'EntityManagerFactory est un singleton et gère sa fermeture.
 */
public class JPAUtil {

    private static EntityManagerFactory entityManagerFactory;

    // Bloc statique pour initialiser l'EntityManagerFactory une seule fois au chargement de la classe
    static {
        try {
            // Le nom "agencePU" doit correspondre au nom défini dans persistence.xml
            entityManagerFactory = Persistence.createEntityManagerFactory("agencePU");
            System.out.println("EntityManagerFactory initialisée avec succès.");
        } catch (Throwable ex) {
            System.err.println("L'initialisation de l'EntityManagerFactory a échoué : " + ex);
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Retourne une nouvelle instance d'EntityManager.
     * Chaque opération ou ensemble d'opérations transactionnelles devrait obtenir son propre EntityManager.
     * @return Une instance d'EntityManager.
     */
    public static EntityManager getEntityManager() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {
            // Re-initialiser si l'EntityManagerFactory a été fermée
            // Ceci est une mesure de sécurité, mais idéalement, elle ne devrait pas être fermée prématurément.
            entityManagerFactory = Persistence.createEntityManagerFactory("agencePU");
        }
        return entityManagerFactory.createEntityManager();
    }

    /**
     * Ferme l'EntityManagerFactory.
     * Cette méthode doit être appelée lors de l'arrêt de l'application (par exemple, dans un ServletContextListener).
     */
    public static void closeEntityManagerFactory() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            System.out.println("EntityManagerFactory fermée.");
        }
    }
}
