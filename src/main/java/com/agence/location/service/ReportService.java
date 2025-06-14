package com.agence.location.service;

import com.agence.location.dao.ClientDAO;
import com.agence.location.dao.LocationDAO;
import com.agence.location.dao.VoitureDAO;
import com.agence.location.model.Client;
import com.agence.location.model.Location;
import com.agence.location.model.Voiture;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la génération de rapports et l'obtention des statistiques du tableau de bord.
 */
public class ReportService {

    private ClientDAO clientDAO;
    private VoitureDAO voitureDAO;
    private LocationDAO locationDAO;

    public ReportService() {
        this.clientDAO = new ClientDAO();
        this.voitureDAO = new VoitureDAO();
        this.locationDAO = new LocationDAO();
    }

    /**
     * Retourne le nombre total de voitures dans l'agence.
     * @return Le nombre total de voitures.
     */
    public int getTotalNumberOfCars() {
        return voitureDAO.findAll().size();
    }

    /**
     * Retourne le nombre de voitures disponibles.
     * @return Le nombre de voitures disponibles.
     */
    public int getNumberOfAvailableCars() {
        return voitureDAO.getVoituresDisponibles().size();
    }

    /**
     * Retourne le nombre de voitures actuellement louées.
     * @return Le nombre de voitures louées.
     */
    public int getNumberOfRentedCars() {
        return voitureDAO.getVoituresLouees().size();
    }

    /**
     * Retourne la liste des locations en cours avec les informations sur les locataires.
     * @return Une liste d'objets Location dont le statut est 'En cours'.
     */
    public List<Location> getRentedCarsWithTenantInfo() {
        // CORRECTION : Utilise findAll() de LocationDAO qui hérite de GenericDAO
        return locationDAO.findAll().stream()
                .filter(l -> "En cours".equals(l.getStatut()))
                .collect(Collectors.toList());
    }

    /**
     * Retourne les N voitures les plus louées (recherchées).
     * @param limit Le nombre de voitures à retourner.
     * @return Une liste de paires (Voiture, Nombre de locations).
     */
    public List<Object[]> getMostSearchedCars(int limit) {
        return locationDAO.getVoituresLesPlusRecherches(limit);
    }

    /**
     * Calcule le bilan financier pour un mois et une année donnés.
     * @param year L'année.
     * @param month Le mois (1-12).
     * @return Le montant total des locations terminées pour le mois.
     */
    public double getMonthlyFinancialReport(int year, int month) {
        return locationDAO.getBilanFinancierMensuel(year, month);
    }

    /**
     * Récupère tous les clients pour l'export.
     * @return La liste de tous les clients.
     */
    public List<Client> getAllClientsForExport() {
        return clientDAO.findAll();
    }
}
