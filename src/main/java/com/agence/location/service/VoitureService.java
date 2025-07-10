package com.agence.location.service;

import com.agence.location.dao.JPAUtil;
import com.agence.location.dao.VoitureDAO;
import com.agence.location.model.Voiture;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service pour la gestion des voitures.
 * Cette classe encapsule la logique métier liée aux opérations CRUD (Création, Lecture, Mise à jour, Suppression)
 * des véhicules de l'agence. Elle gère les transactions de persistance et interagit avec le {@link VoitureDAO}.
 */
public class VoitureService {

    private static final Logger LOGGER = Logger.getLogger(VoitureService.class.getName());

    private VoitureDAO voitureDAO;

    /**
     * Constructeur par défaut.
     * Initialise le Data Access Object (DAO) pour les opérations sur les voitures.
     */
    public VoitureService() {
        this.voitureDAO = new VoitureDAO();
        LOGGER.info("VoitureService initialisé.");
    }

    /**
     * Récupère toutes les voitures enregistrées dans le système.
     * @return Une liste de toutes les voitures, ou une liste vide si aucune voiture n'est trouvée ou en cas d'erreur.
     */
    public List<Voiture> getAllVoitures() {
        LOGGER.info("Tentative de récupération de toutes les voitures.");
        try {
            List<Voiture> voitures = voitureDAO.findAll();
            LOGGER.info("Nombre de voitures récupérées: " + (voitures != null ? voitures.size() : 0));
            return voitures;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de toutes les voitures: " + e.getMessage(), e);
            return new ArrayList<>(); // Retourne une liste vide en cas d'erreur
        }
    }

    /**
     * Récupère une voiture par son numéro d'immatriculation.
     * @param immatriculation Le numéro d'immatriculation de la voiture à rechercher.
     * @return L'objet Voiture correspondant, ou null si non trouvé ou en cas d'erreur.
     */
    public Voiture getVoitureByImmatriculation(String immatriculation) {
        LOGGER.info("Recherche de la voiture par immatriculation: " + immatriculation);
        try {
            return voitureDAO.findById(immatriculation);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération de la voiture par immatriculation " + immatriculation + ": " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Recherche des voitures selon plusieurs critères (marque, kilométrage max, année min, type de carburant, catégorie, statut).
     * Tous les paramètres sont optionnels. Si un paramètre est null ou vide, il n'est pas inclus dans la recherche.
     * @param marque La marque de la voiture.
     * @param kilometrageMax Le kilométrage maximum.
     * @param anneeMiseCirculationMin L'année de mise en circulation minimale.
     * @param typeCarburant Le type de carburant.
     * @param categorie La catégorie de la voiture.
     * @param statut Le statut de la voiture ('Disponible', 'Louee', etc.).
     * @return Une liste de voitures correspondant aux critères, vide si aucune ou en cas d'erreur.
     */
    public List<Voiture> searchVoitures(String marque, Double kilometrageMax, Integer anneeMiseCirculationMin,
                                        String typeCarburant, String categorie, String statut) {
        LOGGER.info("Recherche de voitures avec les critères: Marque=" + marque + ", KilométrageMax=" + kilometrageMax + ", AnnéeMin=" + anneeMiseCirculationMin + ", Carburant=" + typeCarburant + ", Catégorie=" + categorie + ", Statut=" + statut);
        try {
            // Appel à la méthode searchVoitures du DAO avec les 8 paramètres.
            // Les deux derniers (nbPlaces et prixLocationJMax) sont passés comme null
            // pour correspondre à la signature élargie du DAO.
            return voitureDAO.searchVoitures(marque, kilometrageMax, anneeMiseCirculationMin, typeCarburant, categorie, statut, null, null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de voitures: " + e.getMessage(), e);
            return new ArrayList<>(); // Retourne une liste vide en cas d'erreur
        }
    }

    /**
     * Recherche des voitures disponibles selon plusieurs critères,
     * y compris le nombre de places et le prix maximum.
     * Cette méthode est appelée par le ClientDashboardServlet.
     * @param marque La marque de la voiture (optionnel).
     * @param kilometrageMax Le kilométrage maximum (optionnel).
     * @param anneeMiseCirculationMin L'année de mise en circulation minimale (optionnel).
     * @param typeCarburant Le type de carburant (optionnel).
     * @param categorie La catégorie de la voiture (optionnel).
     * @param nbPlaces Le nombre de places minimum (optionnel).
     * @param prixMax Le prix de location journalier maximum (optionnel).
     * @return Une liste de voitures correspondant aux critères et ayant le statut 'Disponible'.
     */
    public List<Voiture> searchAvailableVoitures(String marque, Double kilometrageMax, Integer anneeMiseCirculationMin,
                                                 String typeCarburant, String categorie, Integer nbPlaces, Double prixMax) {
        LOGGER.info("Recherche de voitures disponibles avec filtres: Marque=" + marque + ", KilométrageMax=" + kilometrageMax + ", AnnéeMin=" + anneeMiseCirculationMin + ", Carburant=" + typeCarburant + ", Catégorie=" + categorie + ", NbPlaces=" + nbPlaces + ", PrixMax=" + prixMax);
        try {
            // Déléguer la recherche filtrée au DAO, en passant explicitement "Disponible" pour le statut
            return voitureDAO.searchVoitures(marque, kilometrageMax, anneeMiseCirculationMin,
                                            typeCarburant, categorie, "Disponible", nbPlaces, prixMax);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de voitures disponibles avec filtres: " + e.getMessage(), e);
            return Collections.emptyList(); // Retourne une liste vide en cas d'erreur
        }
    }

    /**
     * Ajoute une nouvelle voiture dans le système.
     * @param voiture L'objet Voiture à ajouter. L'immatriculation doit être unique.
     * @return La voiture ajoutée si l'opération est réussie.
     * @throws RuntimeException Si une voiture avec cette immatriculation existe déjà,
     * ou si une erreur de persistance survient.
     */
    public Voiture addVoiture(Voiture voiture) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            LOGGER.info("Début de la transaction pour l'ajout de la voiture: " + voiture.getImmatriculation());
            transaction.begin();
            // Vérifier si la voiture existe déjà pour éviter les doublons
            if (voitureDAO.findById(voiture.getImmatriculation()) != null) {
                throw new RuntimeException("Une voiture avec cette immatriculation existe déjà: " + voiture.getImmatriculation());
            }
            voitureDAO.persist(em, voiture);
            transaction.commit();
            LOGGER.info("Voiture " + voiture.getImmatriculation() + " ajoutée avec succès.");
            return voiture;
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                LOGGER.warning("Transaction d'ajout de voiture annulée: " + e.getMessage());
            }
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout de la voiture " + voiture.getImmatriculation() + ": " + e.getMessage(), e);
            throw e; // Propage l'exception pour que la servlet puisse la gérer
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                LOGGER.info("EntityManager fermé après addVoiture.");
            }
        }
    }

    /**
     * Met à jour les informations d'une voiture existante.
     * @param voiture L'objet Voiture contenant les informations mises à jour.
     * L'immatriculation est utilisée pour identifier la voiture à mettre à jour.
     * @return La voiture mise à jour si l'opération est réussie.
     * @throws RuntimeException Si la voiture n'est pas trouvée pour la mise à jour,
     * ou si une erreur de persistance survient.
     */
    public Voiture updateVoiture(Voiture voiture) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            LOGGER.info("Début de la transaction pour la mise à jour de la voiture: " + voiture.getImmatriculation());
            transaction.begin();
            // Récupérer l'entité managée pour la mise à jour
            Voiture existingVoiture = em.find(Voiture.class, voiture.getImmatriculation());
            if (existingVoiture == null) {
                throw new RuntimeException("Voiture non trouvée pour la mise à jour: " + voiture.getImmatriculation());
            }
            // Copier les propriétés de l'objet détaché (passé en paramètre) vers l'objet managé
            existingVoiture.setNbPlaces(voiture.getNbPlaces());
            existingVoiture.setMarque(voiture.getMarque());
            existingVoiture.setModele(voiture.getModele());
            existingVoiture.setDateMiseCirculation(voiture.getDateMiseCirculation());
            existingVoiture.setKilometrage(voiture.getKilometrage());
            existingVoiture.setTypeCarburant(voiture.getTypeCarburant());
            existingVoiture.setCategorie(voiture.getCategorie());
            existingVoiture.setPrixLocationJ(voiture.getPrixLocationJ());
            existingVoiture.setStatut(voiture.getStatut());

            voitureDAO.merge(em, existingVoiture); // Fusionne l'entité managée pour persister les changements
            transaction.commit();
            LOGGER.info("Voiture " + voiture.getImmatriculation() + " mise à jour avec succès.");
            return existingVoiture; // Retourne l'entité managée et mise à jour
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                LOGGER.warning("Transaction de mise à jour de voiture annulée: " + e.getMessage());
            }
            LOGGER.log(Level.SEVERE, "Erreur lors de la mise à jour de la voiture " + voiture.getImmatriculation() + ": " + e.getMessage(), e);
            throw e;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                LOGGER.info("EntityManager fermé après updateVoiture.");
            }
        }
    }

    /**
     * Supprime une voiture du système par son numéro d'immatriculation.
     * @param immatriculation L'immatriculation de la voiture à supprimer.
     * @throws RuntimeException Si la voiture n'est pas trouvée pour la suppression,
     * ou si une erreur de persistance survient.
     */
    public void deleteVoiture(String immatriculation) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            LOGGER.info("Début de la transaction pour la suppression de la voiture: " + immatriculation);
            transaction.begin();
            Voiture voitureToDelete = em.find(Voiture.class, immatriculation);
            if (voitureToDelete == null) {
                throw new RuntimeException("Voiture non trouvée pour suppression: " + immatriculation);
            }
            voitureDAO.remove(em, voitureToDelete); // Supprime l'entité managée
            transaction.commit();
            LOGGER.info("Voiture " + immatriculation + " supprimée avec succès.");
        } catch (RuntimeException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                LOGGER.warning("Transaction de suppression de voiture annulée: " + e.getMessage());
            }
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression de la voiture " + immatriculation + ": " + e.getMessage(), e);
            throw e;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                LOGGER.info("EntityManager fermé après deleteVoiture.");
            }
        }
    }
    
    /**
     * Récupère la liste de toutes les voitures dont le statut est 'Disponible'.
     * @return Une liste de voitures disponibles, ou une liste vide si aucune voiture n'est disponible ou en cas d'erreur.
     */
    public List<Voiture> getAvailableVoitures() {
        LOGGER.info("Tentative de récupération des voitures disponibles.");
        try {
            // Délègue la récupération des voitures disponibles au DAO
            List<Voiture> availableVoitures = voitureDAO.getVoituresDisponibles();
            LOGGER.info("Nombre de voitures disponibles récupérées: " + (availableVoitures != null ? availableVoitures.size() : 0));
            return availableVoitures;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des voitures disponibles: " + e.getMessage(), e);
            return new ArrayList<>(); // Retourne une liste vide en cas d'erreur
        }
    }

    /**
     * Récupère le nombre total de voitures disponibles.
     * @return Le nombre de voitures disponibles.
     */
    public long getAvailableVoituresCount() {
        try {
            return voitureDAO.countAvailableVoitures(); 
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du comptage des voitures disponibles: " + e.getMessage(), e);
            return 0;
        }
    }
}
