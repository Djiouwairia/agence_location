package com.agence.location.service;

import com.agence.location.dao.JPAUtil;
import com.agence.location.model.Location;
import com.agence.location.model.Voiture;
import com.agence.location.model.Client;
import com.agence.location.dto.ClientStatsDTO;
import com.agence.location.dto.MonthlyReportDTO;
import com.agence.location.dto.VoitureRentalCountDTO; // NOUVEL IMPORT: DTO pour le compte de locations de voitures
import com.agence.location.dto.FinancialDataDTO;     // NOUVEL IMPORT: DTO pour les données financières

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.time.LocalDateTime; // Import potentiellement utile si vos dates de début sont des LocalDateTime
import java.time.format.DateTimeFormatter; // Pour des formats de date si nécessaire
import java.time.Month; // Pour obtenir le nom du mois dans les DTOs financiers
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Service pour générer divers rapports et statistiques pour le tableau de bord.
 * Ce service centralise la logique de récupération des données agrégées et détaillées
 * pour les vues du tableau de bord et les exports PDF. Il interagit directement avec
 * l'EntityManager via JPAUtil pour exécuter des requêtes.
 */
public class ReportService {

    private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());

    /**
     * Constructeur par défaut.
     * Initialise le logger pour la classe.
     */
    public ReportService() {
        LOGGER.info("ReportService initialisé.");
    }

    /**
     * Compte le nombre total de voitures enregistrées dans la base de données.
     * @return Le nombre total de voitures, ou 0 en cas d'erreur.
     */
    public long getTotalNumberOfCars() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(v) FROM Voiture v", Long.class)
                           .getSingleResult();
            LOGGER.info("Nombre total de voitures: " + count);
            return count;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du comptage total des voitures: " + e.getMessage(), e);
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Compte le nombre de voitures actuellement disponibles (statut 'Disponible').
     * @return Le nombre de voitures avec le statut 'Disponible', ou 0 en cas d'erreur.
     */
    public long getNumberOfAvailableCars() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(v) FROM Voiture v WHERE v.statut = 'Disponible'", Long.class)
                           .getSingleResult();
            LOGGER.info("Nombre de voitures disponibles: " + count);
            return count;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du comptage des voitures disponibles: " + e.getMessage(), e);
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Compte le nombre de voitures actuellement louées (statut 'Louee' ou 'En cours').
     * @return Le nombre de voitures louées, ou 0 en cas d'erreur.
     */
    public long getNumberOfRentedCars() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(v) FROM Voiture v WHERE v.statut IN ('Louee', 'En cours')", Long.class)
                           .getSingleResult();
            LOGGER.info("Nombre de voitures louées: " + count);
            return count;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du comptage des voitures louées: " + e.getMessage(), e);
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Récupère une liste de locations actuellement 'En cours' avec les informations détaillées
     * du client et de la voiture (via FETCH JOIN pour éviter les problèmes N+1).
     * Ces locations représentent les voitures actuellement louées.
     * @return Une liste de Location (avec client et voiture fetchés), vide si aucune ou en cas d'erreur.
     */
    public List<Location> getRentedCarsWithTenantInfo() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Location> rentedCars = new ArrayList<>();
        try {
            TypedQuery<Location> query = em.createQuery(
                "SELECT l FROM Location l JOIN FETCH l.client JOIN FETCH l.voiture WHERE l.statut = 'En cours' ORDER BY l.dateDebut DESC", Location.class);
            rentedCars = query.getResultList();
            LOGGER.info("Nombre de locations en cours récupérées avec infos locataires: " + rentedCars.size());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des voitures louées avec infos locataires: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return rentedCars;
    }

    /**
     * Compte le nombre de demandes de location avec le statut 'En attente'.
     * @return Le nombre de demandes en attente, ou 0 en cas d'erreur ou d'absence de résultats.
     */
    public int getPendingRequestsCount() {
        EntityManager em = JPAUtil.getEntityManager();
        int count = 0;
        try {
            Long result = em.createQuery("SELECT COUNT(l) FROM Location l WHERE l.statut = 'En attente'", Long.class)
                            .getSingleResult();
            if (result != null) {
                count = result.intValue();
            }
            LOGGER.info("Nombre de demandes en attente: " + count);
        } catch (NoResultException e) {
            LOGGER.info("Aucune demande en attente trouvée.");
            return 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du comptage des demandes en attente: " + e.getMessage(), e);
            return 0;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return count;
    }
    
    /**
     * Récupère les X voitures les plus louées (utilisé comme indicateur de "plus recherchées").
     * La logique se base sur le nombre de fois qu'une voiture apparaît dans une location.
     * @param limit Le nombre maximum de voitures à retourner.
     * @return Une liste de voitures les plus louées, vide si aucune ou en cas d'erreur.
     */
    public List<Voiture> getMostSearchedCars(int limit) {
        EntityManager em = JPAUtil.getEntityManager();
        List<Voiture> mostSearched = new ArrayList<>();
        try {
            // Requête JPQL pour obtenir les voitures les plus louées (groupe par voiture, compte les locations)
            TypedQuery<Object[]> query = em.createQuery(
                "SELECT l.voiture, COUNT(l.id) FROM Location l GROUP BY l.voiture ORDER BY COUNT(l.id) DESC", Object[].class);
            query.setMaxResults(limit);
            List<Object[]> results = query.getResultList();

            for (Object[] result : results) {
                if (result[0] instanceof Voiture) {
                    mostSearched.add((Voiture) result[0]);
                }
            }
            LOGGER.info("Nombre de voitures les plus recherchées/louées récupérées: " + mostSearched.size());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des voitures les plus recherchées/louées: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return mostSearched;
    }

    /**
     * Récupère le bilan financier mensuel pour une année et un mois donnés.
     * Inclut le revenu total généré par les locations terminées et le nombre total de locations (tous statuts) pour le mois.
     * @param year L'année du rapport.
     * @param month Le mois du rapport (1-12).
     * @return Un MonthlyReportDTO contenant le revenu total et le nombre total de locations,
     * avec des valeurs par défaut (0.0, 0) en cas d'erreur.
     */
    public MonthlyReportDTO getMonthlyFinancialReport(int year, int month) {
        EntityManager em = JPAUtil.getEntityManager();
        double totalRevenue = 0.0;
        long totalRentals = 0;
        try {
            // Calcul du revenu total pour le mois (uniquement les locations terminées)
            TypedQuery<Double> revenueQuery = em.createQuery(
                "SELECT SUM(l.montantTotal) FROM Location l " +
                "WHERE FUNCTION('YEAR', l.dateDebut) = :year AND FUNCTION('MONTH', l.dateDebut) = :month AND l.statut = 'Terminee'", Double.class);
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

            LOGGER.info("Bilan financier mensuel pour " + month + "/" + year + ": Revenu=" + String.format("%.2f", totalRevenue) + ", Locations=" + totalRentals);
            return new MonthlyReportDTO(totalRevenue, totalRentals);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération du bilan financier mensuel pour " + month + "/" + year + ": " + e.getMessage(), e);
            return new MonthlyReportDTO(0.0, 0); // Retourne un rapport vide en cas d'erreur
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Récupère tous les clients enregistrés.
     * Principalement utilisé pour l'exportation de la liste des clients en PDF.
     * @return Une liste de tous les objets Client, vide si aucun ou en cas d'erreur.
     */
    public List<Client> getAllClientsForExport() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Client> clients = new ArrayList<>();
        try {
            TypedQuery<Client> query = em.createQuery("SELECT c FROM Client c ORDER BY c.nom, c.prenom", Client.class);
            clients = query.getResultList();
            LOGGER.info("Nombre total de clients récupérés pour export: " + clients.size());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de tous les clients pour export: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return clients;
    }

    /**
     * Récupère les voitures les plus louées avec leur nombre de locations pour une période donnée.
     * Les données sont mappées vers le DTO VoitureRentalCountDTO pour le frontend.
     * @param limit Le nombre maximum de voitures à retourner.
     * @param period La période ('all', '3months', '6months', 'currentYear').
     * @return Une liste de VoitureRentalCountDTO, vide si aucune ou en cas d'erreur.
     */
    public List<VoitureRentalCountDTO> getMostRentedCars(int limit, String period) {
        EntityManager em = JPAUtil.getEntityManager();
        List<VoitureRentalCountDTO> results = new ArrayList<>();
        try {
            String jpql = "SELECT l.voiture, COUNT(l.id) FROM Location l ";
            String whereClause = "";
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = null;

            switch (period) {
                case "3months":
                    startDate = endDate.minusMonths(3);
                    whereClause = "WHERE l.dateDebut >= :startDate ";
                    break;
                case "6months":
                    startDate = endDate.minusMonths(6);
                    whereClause = "WHERE l.dateDebut >= :startDate ";
                    break;
                case "currentYear":
                    startDate = LocalDate.of(endDate.getYear(), 1, 1);
                    whereClause = "WHERE FUNCTION('YEAR', l.dateDebut) = :currentYear ";
                    break;
                case "all":
                default:
                    // Pas de clause WHERE pour 'all'
                    break;
            }

            jpql += whereClause + "GROUP BY l.voiture ORDER BY COUNT(l.id) DESC";
            TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);

            if (startDate != null) {
                if (period.equals("currentYear")) {
                    query.setParameter("currentYear", endDate.getYear());
                } else {
                    query.setParameter("startDate", startDate);
                }
            }
            query.setMaxResults(limit);

            List<Object[]> queryResults = query.getResultList();
            for (Object[] row : queryResults) {
                Voiture voiture = (Voiture) row[0];
                Long rentalCount = (Long) row[1];
                results.add(new VoitureRentalCountDTO(
                    voiture.getImmatriculation(),
                    voiture.getMarque(),
                    voiture.getModele(),
                    rentalCount.intValue()
                ));
            }
            LOGGER.info("Récupération des " + limit + " voitures les plus louées pour la période '" + period + "'. Résultats: " + results.size());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des voitures les plus louées: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return results;
    }

    /**
     * Récupère le revenu total agrégé par mois (ou période) pour le graphique financier.
     * @param period La période ('3months', '6months', 'currentYear', 'all').
     * @return Une liste de FinancialDataDTO, vide si aucune ou en cas d'erreur.
     */
    public List<FinancialDataDTO> getFinancialDataOverPeriod(String period) {
        EntityManager em = JPAUtil.getEntityManager();
        List<FinancialDataDTO> financialData = new ArrayList<>();
        try {
            String jpql = "SELECT FUNCTION('YEAR', l.dateDebut), FUNCTION('MONTH', l.dateDebut), SUM(l.montantTotal) " +
                          "FROM Location l WHERE l.statut = 'Terminee' "; // Seules les locations terminées génèrent du revenu

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = null;

            switch (period) {
                case "3months":
                    startDate = endDate.minusMonths(3);
                    jpql += "AND l.dateDebut >= :startDate ";
                    break;
                case "6months":
                    startDate = endDate.minusMonths(6);
                    jpql += "AND l.dateDebut >= :startDate ";
                    break;
                case "currentYear":
                    startDate = LocalDate.of(endDate.getYear(), 1, 1);
                    jpql += "AND FUNCTION('YEAR', l.dateDebut) = :currentYear ";
                    break;
                case "all":
                default:
                    // Pas de clause WHERE supplémentaire pour 'all', récupère tout l'historique
                    break;
            }

            jpql += "GROUP BY FUNCTION('YEAR', l.dateDebut), FUNCTION('MONTH', l.dateDebut) " +
                    "ORDER BY FUNCTION('YEAR', l.dateDebut) ASC, FUNCTION('MONTH', l.dateDebut) ASC";

            TypedQuery<Object[]> query = em.createQuery(jpql, Object[].class);

            if (startDate != null) {
                if (period.equals("currentYear")) {
                    query.setParameter("currentYear", endDate.getYear());
                } else {
                    query.setParameter("startDate", startDate);
                }
            }

            List<Object[]> results = query.getResultList();

            // Format results into FinancialDataDTO
            DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMM yyyy"); // Corrected pattern for year
            for (Object[] row : results) {
                Integer year = (Integer) row[0];
                Integer monthValue = (Integer) row[1];
                Double revenue = (Double) row[2];

                // Handle null revenue (if SUM returns null)
                if (revenue == null) {
                    revenue = 0.0;
                }

                // Construct the label (e.g., "Jul 2025")
                LocalDate date = LocalDate.of(year, monthValue, 1);
                String label = date.format(monthYearFormatter);

                financialData.add(new FinancialDataDTO(label, revenue));
            }
            LOGGER.info("Récupération des données financières pour la période '" + period + "'. Résultats: " + financialData.size());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des données financières par période: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return financialData;
    }
}