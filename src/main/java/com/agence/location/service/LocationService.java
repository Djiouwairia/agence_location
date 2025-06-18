package com.agence.location.service;

import com.agence.location.dao.ClientDAO;
import com.agence.location.dao.LocationDAO;
import com.agence.location.dao.VoitureDAO;
import com.agence.location.dao.JPAUtil; // Pour EntityManager
import com.agence.location.model.Client;
import com.agence.location.model.Location;
import com.agence.location.model.Utilisateur;
import com.agence.location.model.Voiture;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery; // Ajout pour TypedQuery
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service pour la gestion des locations de voitures.
 * Gère les transactions pour les opérations complexes impliquant plusieurs entités.
 */
public class LocationService {

    private static final Logger LOGGER = Logger.getLogger(LocationService.class.getName());

    private ClientDAO clientDAO;
    private VoitureDAO voitureDAO;
    private LocationDAO locationDAO;

    public LocationService() {
        this.clientDAO = new ClientDAO();
        this.voitureDAO = new VoitureDAO();
        this.locationDAO = new LocationDAO();
        LOGGER.info("LocationService initialized.");
    }

    /**
     * Récupère toutes les locations avec les relations Client, Voiture et Utilisateur chargées de manière EAGER.
     * C'est la méthode à utiliser pour les affichages JSP qui nécessitent d'accéder aux propriétés des entités liées.
     * @return Liste de toutes les locations avec relations chargées.
     */
    public List<Location> getAllLocationsWithDetails() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Location> locations = null;
        try {
            // UTILISATION DE JOIN FETCH POUR CHARGER LES RELATIONS EN EAGER
            TypedQuery<Location> query = em.createQuery(
                "SELECT l FROM Location l JOIN FETCH l.client JOIN FETCH l.voiture JOIN FETCH l.utilisateur", 
                Location.class
            );
            locations = query.getResultList();
            LOGGER.info("LocationService: Fetched " + locations.size() + " locations with EAGER details.");
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des locations avec détails: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return locations;
    }

    /**
     * Récupère une location par ID avec les entités associées chargées en EAGER.
     * @param id L'ID de la location.
     * @return La location avec ses détails ou null.
     */
    public Location getLocationByIdWithDetails(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        Location location = null;
        try {
            // Utilisation d'un JOIN FETCH pour charger les entités associées
            TypedQuery<Location> query = em.createQuery(
                "SELECT l FROM Location l JOIN FETCH l.client JOIN FETCH l.voiture JOIN FETCH l.utilisateur WHERE l.id = :id",
                Location.class
            );
            query.setParameter("id", id);
            location = query.getSingleResult();
            LOGGER.info("LocationService: Fetched location ID " + id + " with EAGER details.");
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération de la location par ID avec détails: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return location;
    }

    // --- Les autres méthodes de LocationService (createLocation, recordCarReturn, searchAvailableCars) restent inchangées et sont correctes. ---

    public List<Voiture> searchAvailableCars(String marque, String categorie) {
        LOGGER.info("LocationService: Searching available cars with marque=" + marque + ", categorie=" + categorie);
        return voitureDAO.searchVoitures(marque, null, null, null, categorie, "Disponible");
    }

    public Location createLocation(String clientCin, String voitureImmat, Utilisateur gestionnaire,
                                   LocalDate dateDebut, int nombreJours) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        Location newLocation = null;

        try {
            transaction.begin();
            LOGGER.info("Transaction began for creating new location.");

            Client client = em.find(Client.class, clientCin);
            Voiture voiture = em.find(Voiture.class, voitureImmat);
            Utilisateur managedGestionnaire = em.find(Utilisateur.class, gestionnaire.getId());

            if (client == null) {
                throw new RuntimeException("Client avec CIN " + clientCin + " non trouvé.");
            }
            if (voiture == null) {
                throw new RuntimeException("Voiture avec immatriculation " + voitureImmat + " non trouvée.");
            }
            if (!"Disponible".equals(voiture.getStatut())) {
                throw new RuntimeException("La voiture sélectionnée n'est pas disponible pour la location. Statut actuel: " + voiture.getStatut());
            }
            if (managedGestionnaire == null) {
                 throw new RuntimeException("Utilisateur (gestionnaire) avec ID " + gestionnaire.getId() + " non trouvé.");
            }

            double montantTotal = (double) nombreJours * voiture.getPrixLocationJ();
            double kilometrageDepart = voiture.getKilometrage();
            LocalDate dateRetourPrevue = dateDebut.plusDays(nombreJours);

            newLocation = new Location(client, voiture, managedGestionnaire,
                    dateDebut, nombreJours, dateRetourPrevue, montantTotal, kilometrageDepart);

            locationDAO.persist(em, newLocation);
            LOGGER.info("Location persisted. ID (might be null before commit if not auto-gen): " + newLocation.getId());
            
            voiture.setStatut("Louee");
            voitureDAO.merge(em, voiture);
            LOGGER.info("Voiture " + voiture.getImmatriculation() + " status updated to 'Louee'.");

            transaction.commit();
            LOGGER.info("Location created successfully with ID: " + newLocation.getId());
            return newLocation;

        } catch (RollbackException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            Throwable cause = e.getCause();
            String errorMessage = "Erreur lors de la création de la location (Rollback) : " + (cause != null ? cause.getMessage() : e.getMessage());
            LOGGER.severe(errorMessage);
            throw new RuntimeException(errorMessage, e);
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.severe("RuntimeException lors de la création de la location: " + e.getMessage());
            throw e;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                LOGGER.info("EntityManager closed after createLocation transaction.");
            }
        }
    }

    public void recordCarReturn(Long locationId, double kilometrageRetour) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            LOGGER.info("Transaction began for recording car return for location ID: " + locationId);

            Location location = em.find(Location.class, locationId);

            if (location == null || !"En cours".equals(location.getStatut())) {
                throw new RuntimeException("Location non trouvée ou déjà terminée.");
            }

            Voiture voiture = location.getVoiture();
            if (kilometrageRetour < voiture.getKilometrage()) {
                throw new RuntimeException("Le kilométrage de retour (" + kilometrageRetour + " km) ne peut pas être inférieur au kilométrage actuel de la voiture (" + voiture.getKilometrage() + " km).");
            }

            location.setDateRetourReelle(LocalDate.now());
            location.setKilometrageRetour(kilometrageRetour);
            location.setStatut("Terminee");
            locationDAO.merge(em, location);
            LOGGER.info("Location ID " + location.getId() + " status updated to 'Terminee'.");

            voiture.setKilometrage(kilometrageRetour);
            voiture.setStatut("Disponible");
            voitureDAO.merge(em, voiture);
            LOGGER.info("Voiture " + voiture.getImmatriculation() + " status updated to 'Disponible' and kilometrage updated.");

            transaction.commit();
            LOGGER.info("Car return recorded successfully for location ID: " + locationId);

        } catch (RollbackException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            Throwable cause = e.getCause();
            String errorMessage = "Erreur lors de l'enregistrement du retour (Rollback) : " + (cause != null ? cause.getMessage() : e.getMessage());
            LOGGER.severe(errorMessage);
            throw new RuntimeException(errorMessage, e);
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.severe("RuntimeException lors de l'enregistrement du retour: " + e.getMessage());
            throw e;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                LOGGER.info("EntityManager closed after recordCarReturn transaction.");
            }
        }
    }
}
