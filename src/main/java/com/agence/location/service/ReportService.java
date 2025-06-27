package com.agence.location.service;

import com.agence.location.dao.JPAUtil;
import com.agence.location.model.Location;
import com.agence.location.model.Voiture;
import com.agence.location.model.Client; // Ajouté pour getAllClientsForExport
import com.agence.location.dto.ClientStatsDTO; // Assurez-vous que ce DTO est bien créé
import com.agence.location.dto.MonthlyReportDTO; // Assurez-vous que ce DTO est bien créé

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service pour générer divers rapports et statistiques pour le tableau de bord.
 * Centralise les méthodes de récupération de données pour les vues du tableau de bord et les exports.
 */
public class ReportService {

    private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());

    public ReportService() {
        LOGGER.info("ReportService initialisé.");
    }

    /**
     * Compte le nombre total de voitures dans la base de données.
     * @return Le nombre total de voitures.
     */
    public long getTotalNumberOfCars() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(v) FROM Voiture v", Long.class)
                           .getSingleResult();
            LOGGER.info("Nombre total de voitures: " + count);
            return count;
        } catch (Exception e) {
            LOGGER.severe("Erreur lors du comptage total des voitures: " + e.getMessage());
            throw new RuntimeException("Erreur lors du comptage total des voitures.", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Compte le nombre de voitures actuellement disponibles (statut 'Disponible').
     * @return Le nombre de voitures avec le statut 'Disponible'.
     */
    public long getNumberOfAvailableCars() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(v) FROM Voiture v WHERE v.statut = 'Disponible'", Long.class)
                           .getSingleResult();
            LOGGER.info("Nombre de voitures disponibles: " + count);
            return count;
        } catch (Exception e) {
            LOGGER.severe("Erreur lors du comptage des voitures disponibles: " + e.getMessage());
            throw new RuntimeException("Erreur lors du comptage des voitures disponibles.", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Compte le nombre de voitures actuellement louées (statut 'Louee' ou 'En cours').
     * @return Le nombre de voitures louées.
     */
    public long getNumberOfRentedCars() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Considérer 'En cours' comme louée
            Long count = em.createQuery("SELECT COUNT(v) FROM Voiture v WHERE v.statut IN ('Louee', 'En cours')", Long.class)
                           .getSingleResult();
            LOGGER.info("Nombre de voitures louées: " + count);
            return count;
        } catch (Exception e) {
            LOGGER.severe("Erreur lors du comptage des voitures louées: " + e.getMessage());
            throw new RuntimeException("Erreur lors du comptage des voitures louées.", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Récupère une liste de locations actuellement 'En cours' avec les informations du locataire.
     * Ces locations représentent les voitures actuellement louées.
     * @return Une liste de Location (avec client et voiture fetchés).
     */
    public List<Location> getRentedCarsWithTenantInfo() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Location> rentedCars = null;
        try {
            // Utilise JOIN FETCH pour charger le client et la voiture en même temps
            TypedQuery<Location> query = em.createQuery(
                "SELECT l FROM Location l JOIN FETCH l.client JOIN FETCH l.voiture WHERE l.statut = 'En cours' ORDER BY l.dateDebut DESC", Location.class);
            rentedCars = query.getResultList();
            LOGGER.info("Nombre de locations en cours récupérées: " + (rentedCars != null ? rentedCars.size() : "0"));
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des voitures louées avec infos locataires: " + e.getMessage());
            return new ArrayList<>(); // Retourne une liste vide en cas d'erreur
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return rentedCars;
    }

    /**
     * Compte le nombre de demandes de location avec le statut 'En attente'.
     * @return Le nombre de demandes en attente.
     */
    public int getPendingRequestsCount() {
        EntityManager em = JPAUtil.getEntityManager();
        int count = 0;
        try {
            Long result = em.createQuery("SELECT COUNT(l) FROM Location l WHERE l.statut = 'En attente'", Long.class)
                            .getSingleResult();
            count = result.intValue();
            LOGGER.info("Nombre de demandes en attente: " + count);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors du comptage des demandes en attente: " + e.getMessage());
            throw new RuntimeException("Erreur lors du comptage des demandes en attente.", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return count;
    }
    
    /**
     * Récupère les X voitures les plus louées (utilisé comme "plus recherchées" pour l'exemple).
     * @param limit Le nombre de voitures à retourner.
     * @return Une liste de voitures.
     */
    public List<Voiture> getMostSearchedCars(int limit) {
        EntityManager em = JPAUtil.getEntityManager();
        List<Voiture> mostSearched = new ArrayList<>();
        try {
            // JPQL pour obtenir les voitures les plus louées (un proxy pour "plus recherchées")
            TypedQuery<Object[]> query = em.createQuery(
                "SELECT l.voiture, COUNT(l.id) FROM Location l GROUP BY l.voiture ORDER BY COUNT(l.id) DESC", Object[].class);
            query.setMaxResults(limit);
            List<Object[]> results = query.getResultList();

            for (Object[] result : results) {
                if (result[0] instanceof Voiture) { // S'assurer que le type est correct
                    mostSearched.add((Voiture) result[0]);
                }
            }
            LOGGER.info("Nombre de voitures les plus recherchées/louées récupérées: " + mostSearched.size());

        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des voitures les plus recherchées/louées: " + e.getMessage());
            return new ArrayList<>(); // Retourne une liste vide en cas d'erreur
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return mostSearched;
    }

    /**
     * Récupère le bilan financier mensuel.
     * @param year L'année.
     * @param month Le mois (1-12).
     * @return Un DTO contenant le total des revenus et des locations.
     */
    public MonthlyReportDTO getMonthlyFinancialReport(int year, int month) {
        EntityManager em = JPAUtil.getEntityManager();
        double totalRevenue = 0.0;
        long totalRentals = 0;
        try {
            // Calcul du revenu total pour le mois (locations terminées)
            TypedQuery<Double> revenueQuery = em.createQuery(
                "SELECT SUM(l.montantTotal) FROM Location l WHERE FUNCTION('YEAR', l.dateDebut) = :year AND FUNCTION('MONTH', l.dateDebut) = :month AND l.statut = 'Terminee'", Double.class);
            revenueQuery.setParameter("year", year);
            revenueQuery.setParameter("month", month);
            Double resultRevenue = revenueQuery.getSingleResult();
            if (resultRevenue != null) {
                totalRevenue = resultRevenue;
            }

            // Calcul du nombre total de locations pour le mois (tous statuts)
            TypedQuery<Long> rentalsQuery = em.createQuery(
                "SELECT COUNT(l) FROM Location l WHERE FUNCTION('YEAR', l.dateDebut) = :year AND FUNCTION('MONTH', l.dateDebut) = :month", Long.class);
            rentalsQuery.setParameter("year", year);
            rentalsQuery.setParameter("month", month);
            Long resultRentals = rentalsQuery.getSingleResult();
            if (resultRentals != null) {
                totalRentals = resultRentals;
            }

            LOGGER.info("Bilan financier mensuel pour " + month + "/" + year + " : Revenu=" + totalRevenue + ", Locations=" + totalRentals);
            return new MonthlyReportDTO(totalRevenue, totalRentals);

        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération du bilan financier mensuel: " + e.getMessage());
            return new MonthlyReportDTO(0.0, 0); // Retourne un rapport vide en cas d'erreur
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Récupère tous les clients enregistrés.
     * Utilisé pour l'exportation de la liste des clients en PDF.
     * @return Une liste de tous les objets Client.
     */
    public List<Client> getAllClientsForExport() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Client> clients = null;
        try {
            TypedQuery<Client> query = em.createQuery("SELECT c FROM Client c ORDER BY c.nom, c.prenom", Client.class);
            clients = query.getResultList();
            LOGGER.info("Nombre total de clients récupérés pour export: " + (clients != null ? clients.size() : "0"));
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération de tous les clients pour export: " + e.getMessage());
            return new ArrayList<>(); // Retourne une liste vide en cas d'erreur
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return clients;
    }
}
