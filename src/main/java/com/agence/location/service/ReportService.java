package com.agence.location.service;

import com.agence.location.dao.ClientDAO;
import com.agence.location.dao.LocationDAO;
import com.agence.location.dao.VoitureDAO;
import com.agence.location.dao.JPAUtil; // Import pour EntityManager
import com.agence.location.model.Client;
import com.agence.location.model.Location;
import com.agence.location.model.Voiture;
import com.agence.location.model.Utilisateur; // Import nécessaire si Utilisateur est fetché

import javax.persistence.EntityManager; // Import pour EntityManager
import javax.persistence.TypedQuery; // Import pour TypedQuery
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger; // Import pour Logger
import java.util.stream.Collectors;

/**
 * Service pour la génération de rapports et l'obtention des statistiques du tableau de bord.
 */
public class ReportService {

    private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName()); // Initialisation du logger

    private ClientDAO clientDAO;
    private VoitureDAO voitureDAO;
    private LocationDAO locationDAO;

    public ReportService() {
        this.clientDAO = new ClientDAO();
        this.voitureDAO = new VoitureDAO();
        this.locationDAO = new LocationDAO();
        LOGGER.info("ReportService initialized.");
    }

    /**
     * Retourne le nombre total de voitures dans l'agence.
     * @return Le nombre total de voitures.
     */
    public long getTotalNumberOfCars() { // Changé en long pour correspondre au COUNT(*) en JPQL
        EntityManager em = JPAUtil.getEntityManager();
        try {
            long count = em.createQuery("SELECT COUNT(v) FROM Voiture v", Long.class).getSingleResult();
            LOGGER.info("Total number of cars: " + count);
            return count;
        } catch (Exception e) {
            LOGGER.severe("Error getting total number of cars: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Retourne le nombre de voitures disponibles.
     * @return Le nombre de voitures disponibles.
     */
    public long getNumberOfAvailableCars() { // Changé en long
        EntityManager em = JPAUtil.getEntityManager();
        try {
            long count = em.createQuery("SELECT COUNT(v) FROM Voiture v WHERE v.statut = 'Disponible'", Long.class).getSingleResult();
            LOGGER.info("Number of available cars: " + count);
            return count;
        } catch (Exception e) {
            LOGGER.severe("Error getting number of available cars: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Retourne le nombre de voitures actuellement louées (basé sur le statut des locations).
     * @return Le nombre de voitures louées.
     */
    public long getNumberOfRentedCars() { // Changé en long
        EntityManager em = JPAUtil.getEntityManager();
        try {
            long count = em.createQuery("SELECT COUNT(l) FROM Location l WHERE l.statut = 'En cours'", Long.class).getSingleResult();
            LOGGER.info("Number of rented cars (active locations): " + count);
            return count;
        } catch (Exception e) {
            LOGGER.severe("Error getting number of rented cars: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Retourne la liste des locations en cours avec les informations sur les locataires (client, voiture, gestionnaire).
     * Utilise JOIN FETCH pour éviter les LazyInitializationException.
     * @return Une liste d'objets Location dont le statut est 'En cours', avec les relations chargées.
     */
    public List<Location> getRentedCarsWithTenantInfo() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Location> rentedLocations = null;
        try {
            // CORRECTION CRUCIALE : Utilisation de JOIN FETCH pour charger les relations EAGERLY
            TypedQuery<Location> query = em.createQuery(
                "SELECT l FROM Location l JOIN FETCH l.client JOIN FETCH l.voiture JOIN FETCH l.utilisateur WHERE l.statut = 'En cours'",
                Location.class
            );
            rentedLocations = query.getResultList();
            LOGGER.info("Fetched " + (rentedLocations != null ? rentedLocations.size() : 0) + " rented cars with tenant info (EAGER).");
        } catch (Exception e) {
            LOGGER.severe("Error getting rented cars with tenant info: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return rentedLocations;
    }

    /**
     * Retourne les N voitures les plus louées (recherchées).
     * @param limit Le nombre de voitures à retourner.
     * @return Une liste de paires (Voiture, Nombre de locations).
     */
    public List<Object[]> getMostSearchedCars(int limit) {
        // Cette méthode doit être implémentée dans LocationDAO (ou VoitureDAO)
        // et doit utiliser JOIN FETCH si elle retourne des entités complexes.
        // Pour l'instant, je m'assure juste que l'appel est là.
        // Assurez-vous que locationDAO.getVoituresLesPlusRecherches(limit) renvoie des Object[]
        LOGGER.info("Getting " + limit + " most searched cars (requires DAO implementation).");
        return locationDAO.getVoituresLesPlusRecherches(limit);
    }

    /**
     * Calcule le bilan financier pour un mois et une année donnés.
     * @param year L'année.
     * @param month Le mois (1-12).
     * @return Le montant total des locations terminées pour le mois.
     */
    public double getMonthlyFinancialReport(int year, int month) {
        // Cette méthode doit être implémentée dans LocationDAO
        LOGGER.info("Getting monthly financial report for " + month + "/" + year + " (requires DAO implementation).");
        return locationDAO.getBilanFinancierMensuel(year, month);
    }

    /**
     * Récupère tous les clients pour l'export.
     * @return La liste de tous les clients.
     */
    public List<Client> getAllClientsForExport() {
        LOGGER.info("Fetching all clients for export.");
        return clientDAO.findAll();
    }
}
