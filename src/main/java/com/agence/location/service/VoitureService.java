package com.agence.location.service;

import com.agence.location.dao.JPAUtil;
import com.agence.location.dao.VoitureDAO;
import com.agence.location.model.Voiture;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.List;

/**
 * Service pour la gestion des voitures.
 * Gère les transactions pour les opérations de modification.
 */
public class VoitureService {

    private VoitureDAO voitureDAO;

    public VoitureService() {
        this.voitureDAO = new VoitureDAO();
    }

    public List<Voiture> getAllVoitures() {
        return voitureDAO.findAll();
    }

    public Voiture getVoitureByImmatriculation(String immatriculation) {
        return voitureDAO.findById(immatriculation);
    }

    public List<Voiture> searchVoitures(String marque, Double kilometrageMax, Integer anneeMiseCirculationMin,
                                        String typeCarburant, String categorie, String statut) {
        return voitureDAO.searchVoitures(marque, kilometrageMax, anneeMiseCirculationMin, typeCarburant, categorie, statut);
    }

    /**
     * Ajoute une nouvelle voiture.
     * @param voiture La voiture à ajouter.
     * @return La voiture ajoutée.
     * @throws RuntimeException Si la voiture existe déjà ou si une erreur de persistance survient.
     */
    public Voiture addVoiture(Voiture voiture) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            // Vérifier si la voiture existe déjà
            if (voitureDAO.findById(voiture.getImmatriculation()) != null) {
                throw new RuntimeException("Une voiture avec cette immatriculation existe déjà.");
            }
            voitureDAO.persist(em, voiture);
            transaction.commit();
            return voiture;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Met à jour les informations d'une voiture existante.
     * @param voiture La voiture avec les informations mises à jour.
     * @return La voiture mise à jour.
     * @throws RuntimeException Si la voiture n'est pas trouvée ou si une erreur de persistance survient.
     */
    public Voiture updateVoiture(Voiture voiture) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            // Récupérer l'entité managée pour la mise à jour
            Voiture existingVoiture = em.find(Voiture.class, voiture.getImmatriculation());
            if (existingVoiture == null) {
                throw new RuntimeException("Voiture non trouvée pour la mise à jour.");
            }
            // Copier les propriétés de l'objet détaché vers l'objet managé
            existingVoiture.setNbPlaces(voiture.getNbPlaces());
            existingVoiture.setMarque(voiture.getMarque());
            existingVoiture.setModele(voiture.getModele());
            existingVoiture.setDateMiseCirculation(voiture.getDateMiseCirculation());
            existingVoiture.setKilometrage(voiture.getKilometrage());
            existingVoiture.setTypeCarburant(voiture.getTypeCarburant());
            existingVoiture.setCategorie(voiture.getCategorie());
            existingVoiture.setPrixLocationJ(voiture.getPrixLocationJ());
            existingVoiture.setStatut(voiture.getStatut());

            voitureDAO.merge(em, existingVoiture); // Fusionne l'entité
            transaction.commit();
            return existingVoiture; // Retourne l'entité managée et mise à jour
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Supprime une voiture.
     * @param immatriculation L'immatriculation de la voiture à supprimer.
     * @throws RuntimeException Si la voiture n'est pas trouvée ou si une erreur de persistance survient.
     */
    public void deleteVoiture(String immatriculation) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Voiture voitureToDelete = em.find(Voiture.class, immatriculation);
            if (voitureToDelete == null) {
                throw new RuntimeException("Voiture non trouvée pour suppression.");
            }
            voitureDAO.remove(em, voitureToDelete);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
    
    public List<Voiture> getAvailableVoitures() {
        // Le LOGGER n'était pas importé, je l'ajoute si vous voulez des logs
        // import java.util.logging.Logger;
        // private static final Logger LOGGER = Logger.getLogger(VoitureService.class.getName());
        // LOGGER.info("Récupération des voitures disponibles.");
        return voitureDAO.getVoituresDisponibles();
    }
}
