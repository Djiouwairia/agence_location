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
import java.time.LocalDate;
import java.util.List;

/**
 * Service pour la gestion des locations de voitures.
 * Gère les transactions pour les opérations complexes impliquant plusieurs entités.
 */
public class LocationService {

    private ClientDAO clientDAO;
    private VoitureDAO voitureDAO;
    private LocationDAO locationDAO;

    public LocationService() {
        this.clientDAO = new ClientDAO();
        this.voitureDAO = new VoitureDAO();
        this.locationDAO = new LocationDAO();
    }

    public List<Location> getAllLocations() {
        return locationDAO.findAll();
    }

    public Location getLocationById(Long id) {
        return locationDAO.findById(id);
    }

    public List<Voiture> searchAvailableCars(String marque, String categorie) {
        // Le statut "Disponible" est hardcodé ici car c'est une recherche de voiture pour location
        return voitureDAO.searchVoitures(marque, null, null, null, categorie, "Disponible");
    }

    /**
     * Enregistre une nouvelle location.
     * Cette opération implique la création d'une Location et la mise à jour du statut d'une Voiture,
     * elle est donc gérée dans une transaction unique.
     * @param clientCin Le CIN du client.
     * @param voitureImmat L'immatriculation de la voiture.
     * @param gestionnaire L'utilisateur (gestionnaire) effectuant la location.
     * @param dateDebut La date de début de la location.
     * @param nombreJours Le nombre de jours de location.
     * @return L'objet Location nouvellement créé.
     * @throws RuntimeException Si le client/voiture n'est pas trouvé ou la voiture n'est pas disponible.
     */
    public Location createLocation(String clientCin, String voitureImmat, Utilisateur gestionnaire,
                                   LocalDate dateDebut, int nombreJours) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        Location newLocation = null;

        try {
            transaction.begin();

            Client client = em.find(Client.class, clientCin); // Récupère le client dans la même transaction
            Voiture voiture = em.find(Voiture.class, voitureImmat); // Récupère la voiture dans la même transaction

            if (client == null) {
                throw new RuntimeException("Client avec CIN " + clientCin + " non trouvé.");
            }
            if (voiture == null) {
                throw new RuntimeException("Voiture avec immatriculation " + voitureImmat + " non trouvée.");
            }
            if (!"Disponible".equals(voiture.getStatut())) {
                throw new RuntimeException("La voiture sélectionnée n'est pas disponible pour la location.");
            }

            double montantTotal = (double) nombreJours * voiture.getPrixLocationJ();
            double kilometrageDepart = voiture.getKilometrage();

            LocalDate dateRetourPrevue = dateDebut.plusDays(nombreJours);

            newLocation = new Location(client, voiture, gestionnaire,
                    dateDebut, nombreJours, dateRetourPrevue, montantTotal, kilometrageDepart);

            locationDAO.persist(em, newLocation); // Persiste la location
            
            // Mettre à jour le statut de la voiture dans la même transaction
            voiture.setStatut("Louee");
            voitureDAO.merge(em, voiture); // Fusionne les changements de la voiture

            transaction.commit();
            return newLocation;

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
     * Enregistre le retour d'une voiture.
     * Cette opération implique la mise à jour d'une Location et d'une Voiture,
     * elle est donc gérée dans une transaction unique.
     * @param locationId L'ID de la location à terminer.
     * @param kilometrageRetour Le nouveau kilométrage de la voiture au retour.
     * @throws RuntimeException Si la location n'est pas trouvée ou si le kilométrage est invalide.
     */
    public void recordCarReturn(Long locationId, double kilometrageRetour) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            Location location = em.find(Location.class, locationId); // Récupère la location dans la même transaction

            if (location == null || !"En cours".equals(location.getStatut())) {
                throw new RuntimeException("Location non trouvée ou déjà terminée.");
            }

            Voiture voiture = location.getVoiture(); // Récupère la voiture via la relation dans la même transaction
            if (kilometrageRetour < voiture.getKilometrage()) {
                throw new RuntimeException("Le kilométrage de retour (" + kilometrageRetour + " km) ne peut pas être inférieur au kilométrage actuel de la voiture (" + voiture.getKilometrage() + " km).");
            }

            // Mettre à jour la location
            location.setDateRetourReelle(LocalDate.now());
            location.setKilometrageRetour(kilometrageRetour);
            location.setStatut("Terminee");
            locationDAO.merge(em, location); // Fusionne les changements de la location

            // Mettre à jour la voiture
            voiture.setKilometrage(kilometrageRetour);
            voiture.setStatut("Disponible");
            voitureDAO.merge(em, voiture); // Fusionne les changements de la voiture

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
}
