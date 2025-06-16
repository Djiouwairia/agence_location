package com.agence.location.dao;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collections; // Import nécessaire
import java.util.List;

/**
 * Classe DAO générique pour les opérations CRUD de base sur n'importe quelle entité JPA.
 * Ne gère PLUS les transactions. La gestion des transactions est déléguée à la couche Service.
 * Un EntityManager est obtenu pour chaque opération et fermé, à moins qu'il ne soit passé en paramètre.
 *
 * @param <T> Le type de l'entité (ex: Client, Voiture)
 * @param <ID> Le type de l'identifiant de l'entité (ex: String pour Client, Long pour Utilisateur)
 */
public abstract class GenericDAO<T, ID> {

    private Class<T> entityClass; // Le type de l'entité gérée par ce DAO

    /**
     * Constructeur pour initialiser le DAO avec la classe de l'entité.
     * @param entityClass La classe de l'entité (par exemple, Client.class)
     */
    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Recherche une entité par son identifiant.
     * @param id L'identifiant de l'entité.
     * @return L'entité trouvée, ou null si non trouvée.
     */
    public T findById(ID id) {
        EntityManager em = JPAUtil.getEntityManager(); // Obtient un EntityManager
        T entity = null;
        try {
            entity = em.find(entityClass, id);
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche par ID pour " + entityClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close(); // Ferme l'EntityManager
            }
        }
        return entity;
    }

    /**
     * Récupère toutes les entités de ce type.
     * @return Une liste de toutes les entités. Retourne une liste vide si aucune entité n'est trouvée ou en cas d'erreur.
     */
    public List<T> findAll() {
        EntityManager em = JPAUtil.getEntityManager(); // Obtient un EntityManager
        List<T> entities = Collections.emptyList(); // Initialisé à une liste vide, PAS à null
        try {
            entities = em.createQuery("SELECT o FROM " + entityClass.getSimpleName() + " o", entityClass).getResultList();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de toutes les entités pour " + entityClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            // En cas d'exception, 'entities' reste une liste vide, évitant les NullPointerException.
        } finally {
            if (em != null && em.isOpen()) {
                em.close(); // Ferme l'EntityManager
            }
        }
        return entities; // Retourne toujours une List (vide ou avec des données), jamais null.
    }

    /**
     * Persiste une nouvelle entité.
     * Cette méthode suppose qu'une transaction est active et gérée par la couche service.
     * @param em L'EntityManager courant de la transaction.
     * @param entity L'entité à persister.
     */
    public void persist(EntityManager em, T entity) {
        em.persist(entity);
    }

    /**
     * Met à jour une entité existante.
     * Cette méthode suppose qu'une transaction est active et gérée par la couche service.
     * @param em L'EntityManager courant de la transaction.
     * @param entity L'entité à fusionner (mettre à jour).
     * @return L'entité fusionnée.
     */
    public T merge(EntityManager em, T entity) {
        return em.merge(entity);
    }

    /**
     * Supprime une entité de la base de données.
     * Cette méthode suppose qu'une transaction est active et gérée par la couche service.
     * @param em L'EntityManager courant de la transaction.
     * @param entity L'entité à supprimer.
     */
    public void remove(EntityManager em, T entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
