package com.agence.location.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.NoResultException; // Pour gérer les cas où aucune entité n'est trouvée
import java.util.List;

/**
 * Classe DAO générique pour les opérations CRUD de base sur n'importe quelle entité JPA.
 *
 * @param <T> Le type de l'entité (ex: Client, Voiture)
 * @param <ID> Le type de l'identifiant de l'entité (ex: String pour Client, Long pour Utilisateur)
 */
public abstract class GenericDAO<T, ID> {

    // EntityManagerFactory est coûteuse à créer, donc on la crée une seule fois.
    // Le nom "agencePU" doit correspondre au nom défini dans persistence.xml.
    protected static EntityManagerFactory emf = Persistence.createEntityManagerFactory("agencePU");

    // L'EntityManager est l'interface principale pour interagir avec la persistance.
    // Il doit être géré avec soin (ouvert et fermé pour chaque opération ou cycle de requête).
    protected EntityManager em;

    private Class<T> entityClass; // Le type de l'entité gérée par ce DAO

    /**
     * Constructeur pour initialiser le DAO avec la classe de l'entité.
     * @param entityClass La classe de l'entité (par exemple, Client.class)
     */
    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Obtient une instance d'EntityManager.
     * Crée une nouvelle instance si l'actuelle est nulle ou fermée.
     * @return Un EntityManager actif.
     */
    protected EntityManager getEntityManager() {
        if (em == null || !em.isOpen()) {
            em = emf.createEntityManager();
        }
        return em;
    }

    /**
     * Recherche une entité par son identifiant.
     * @param id L'identifiant de l'entité.
     * @return L'entité trouvée, ou null si non trouvée.
     */
    public T findById(ID id) {
        em = getEntityManager();
        T entity = null;
        try {
            entity = em.find(entityClass, id);
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche par ID pour " + entityClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return entity;
    }

    /**
     * Récupère toutes les entités de ce type.
     * @return Une liste de toutes les entités.
     */
    public List<T> findAll() {
        em = getEntityManager();
        List<T> entities = null;
        try {
            // Requête JPQL pour récupérer toutes les entités du type spécifié
            entities = em.createQuery("SELECT o FROM " + entityClass.getSimpleName() + " o", entityClass).getResultList();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de toutes les entités pour " + entityClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return entities;
    }

    /**
     * Sauvegarde une entité (persiste une nouvelle entité ou met à jour une existante).
     * @param entity L'entité à sauvegarder.
     * @return L'entité sauvegardée (peut être une instance fusionnée pour les mises à jour).
     * @throws RuntimeException Si une erreur de persistance survient.
     */
    public T save(T entity) {
        em = getEntityManager();
        em.getTransaction().begin();
        try {
            // Si l'entité est déjà gérée (existante dans le contexte de persistance), elle est fusionnée.
            // Sinon (nouvelle entité), elle est persistée.
            if (em.contains(entity)) {
                entity = em.merge(entity); // Pour les mises à jour
            } else {
                em.persist(entity); // Pour les nouvelles entités
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Annule la transaction en cas d'erreur
            }
            System.err.println("Erreur lors de la sauvegarde de l'entité " + entityClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la sauvegarde de l'entité: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return entity;
    }

    /**
     * Supprime une entité de la base de données.
     * @param entity L'entité à supprimer.
     * @throws RuntimeException Si une erreur de persistance survient.
     */
    public void delete(T entity) {
        em = getEntityManager();
        em.getTransaction().begin();
        try {
            // S'assure que l'entité est gérée avant de la supprimer
            em.remove(em.contains(entity) ? entity : em.merge(entity));
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback(); // Annule la transaction en cas d'erreur
            }
            System.err.println("Erreur lors de la suppression de l'entité " + entityClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la suppression de l'entité: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Méthode statique pour fermer l'EntityManagerFactory lorsque l'application s'arrête.
     * Il est crucial d'appeler cette méthode pour libérer les ressources.
     * Ceci pourrait être fait dans un ServletContextListener.
     */
    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            System.out.println("EntityManagerFactory fermée.");
        }
    }
}
