package com.agence.location.dao;

import com.agence.location.model.Location;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Logger;

/**
 * DAO (Data Access Object) spécifique pour l'entité Location.
 * Étend GenericDAO pour hériter des opérations CRUD de base.
 * Contient des méthodes spécifiques pour les requêtes sur les locations.
 */
public class LocationDAO extends GenericDAO<Location, Long> {

    private static final Logger LOGGER = Logger.getLogger(LocationDAO.class.getName());

    public LocationDAO() {
        super(Location.class); // Indique à GenericDAO que ce DAO gère l'entité Location
    }

    /**
     * Récupère toutes les locations en incluant les détails du client, de la voiture et de l'utilisateur (gestionnaire)
     * via FETCH JOIN pour éviter les problèmes de LazyInitializationException.
     * @return Une liste de toutes les locations avec leurs entités associées.
     */
    public List<Location> findAllWithDetails() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Location> locations = null;
        try {
            // Utilise LEFT JOIN FETCH pour l'utilisateur car il peut être null (pour les demandes client initiales)
            TypedQuery<Location> query = em.createQuery(
                "SELECT l FROM Location l LEFT JOIN FETCH l.client LEFT JOIN FETCH l.voiture LEFT JOIN FETCH l.utilisateur ORDER BY l.id DESC", Location.class);
            locations = query.getResultList();
            LOGGER.info("findAllWithDetails: " + (locations != null ? locations.size() : "0") + " locations récupérées avec détails.");
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération de toutes les locations avec détails: " + e.getMessage());
            throw new RuntimeException("Erreur DAO lors de la récupération des locations.", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return locations;
    }

    /**
     * Récupère une location par son ID en incluant les détails du client, de la voiture et de l'utilisateur (gestionnaire)
     * via FETCH JOIN pour éviter les problèmes de LazyInitializationException.
     * @param id L'ID de la location à rechercher.
     * @return La location trouvée, ou null si aucune location avec cet ID n'est trouvée.
     */
    public Location findByIdWithDetails(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        Location location = null;
        try {
            // Utilise LEFT JOIN FETCH pour l'utilisateur car il peut être null
            TypedQuery<Location> query = em.createQuery(
                "SELECT l FROM Location l LEFT JOIN FETCH l.client LEFT JOIN FETCH l.voiture LEFT JOIN FETCH l.utilisateur WHERE l.id = :id", Location.class);
            query.setParameter("id", id);
            location = query.getSingleResult();
            LOGGER.info("findByIdWithDetails ID " + id + ": " + (location != null ? "Trouvé" : "Non trouvé"));
        } catch (NoResultException e) {
            LOGGER.warning("Aucune location trouvée avec l'ID: " + id);
            return null;
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la recherche de la location par ID avec détails: " + id + " - " + e.getMessage());
            throw new RuntimeException("Erreur DAO lors de la recherche de la location.", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return location;
    }

    // Les méthodes génériques (persist, merge, remove) sont héritées de GenericDAO.
}
