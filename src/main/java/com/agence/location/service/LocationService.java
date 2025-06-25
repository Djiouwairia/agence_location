package com.agence.location.service;

import com.agence.location.dao.ClientDAO;
import com.agence.location.dao.LocationDAO;
import com.agence.location.dao.VoitureDAO;
import com.agence.location.dao.JPAUtil;
import com.agence.location.model.Client;
import com.agence.location.model.Location;
import com.agence.location.model.Utilisateur;
import com.agence.location.model.Voiture;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
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
        LOGGER.info("LocationService initialisé.");
    }

    /**
     * Récupère toutes les locations avec les relations Client, Voiture, Utilisateur.
     * @return Une liste de toutes les locations.
     */
    public List<Location> getAllLocationsWithDetails() {
        LOGGER.info("Récupération de toutes les locations avec détails.");
        EntityManager em = JPAUtil.getEntityManager();
        List<Location> locations = null;
        try {
            TypedQuery<Location> query = em.createQuery(
                "SELECT l FROM Location l JOIN FETCH l.client JOIN FETCH l.voiture LEFT JOIN FETCH l.utilisateur ORDER BY l.id DESC", Location.class);
            locations = query.getResultList();
            LOGGER.info("Nombre total de locations récupérées (avec détails): " + (locations != null ? locations.size() : "0"));
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération de toutes les locations avec détails: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la récupération de toutes les locations.", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                LOGGER.info("EntityManager fermé après getAllLocationsWithDetails.");
            }
        }
        return locations;
    }


    /**
     * Récupère une location par son ID avec ses détails (Client, Voiture, Utilisateur).
     * @param id L'ID de la location.
     * @return La location trouvée, ou null si non trouvée.
     */
    public Location getLocationByIdWithDetails(Long id) {
        LOGGER.info("Récupération de la location par ID: " + id);
        EntityManager em = JPAUtil.getEntityManager();
        Location location = null;
        try {
            TypedQuery<Location> query = em.createQuery(
                "SELECT l FROM Location l JOIN FETCH l.client JOIN FETCH l.voiture LEFT JOIN FETCH l.utilisateur WHERE l.id = :id", Location.class);
            query.setParameter("id", id);
            location = query.getSingleResult();
            LOGGER.info("Location ID " + id + " trouvée: " + (location != null ? location.getStatut() : "null"));
        } catch (javax.persistence.NoResultException e) {
            LOGGER.warning("Aucune location trouvée avec l'ID: " + id);
            return null;
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération de la location par ID: " + id + " - " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la récupération de la location: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                LOGGER.info("EntityManager fermé après getLocationByIdWithDetails.");
            }
        }
        return location;
    }


    /**
     * Ajoute une nouvelle location.
     * Assure que la voiture passe au statut 'Louee'.
     * @param location L'objet Location à enregistrer.
     * @param utilisateur L'utilisateur (gestionnaire) effectuant la location.
     * @return La location persistée.
     * @throws RuntimeException Si la voiture n'est pas disponible ou toute autre erreur de persistance.
     */
    public Location addLocation(Location location, Utilisateur utilisateur) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            LOGGER.info("Début de la transaction pour addLocation.");
            transaction.begin();

            Client managedClient = em.find(Client.class, location.getClient().getCin());
            Voiture managedVoiture = em.find(Voiture.class, location.getVoiture().getImmatriculation());
            Utilisateur managedUtilisateur = em.find(Utilisateur.class, utilisateur.getId());

            if (managedClient == null) {
                throw new RuntimeException("Client non trouvé.");
            }
            if (managedVoiture == null) {
                throw new RuntimeException("Voiture non trouvée.");
            }
            if (!"Disponible".equals(managedVoiture.getStatut())) {
                throw new RuntimeException("La voiture n'est pas disponible pour la location.");
            }
            if (managedUtilisateur == null) {
                throw new RuntimeException("Utilisateur (gestionnaire) non trouvé.");
            }

            managedVoiture.setStatut("Louee");
            voitureDAO.merge(em, managedVoiture);

            location.setClient(managedClient);
            location.setVoiture(managedVoiture);
            location.setUtilisateur(managedUtilisateur);

            locationDAO.persist(em, location);
            transaction.commit();
            LOGGER.info("Location ajoutée avec succès pour le client " + location.getClient().getCin() + " et voiture " + location.getVoiture().getImmatriculation());
            return location;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
                LOGGER.severe("Transaction addLocation ROLLED BACK: " + e.getMessage());
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
                LOGGER.info("EntityManager fermé après addLocation.");
            }
        }
    }

    /**
     * Crée une nouvelle location à partir des CIN client, immatriculation voiture, etc.
     * Cette méthode encapsule la logique métier pour l'ajout par le gestionnaire.
     * @param clientCin CIN du client.
     * @param voitureImmat Immatriculation de la voiture.
     * @param gestionnaire Utilisateur gestionnaire.
     * @param dateDebut Date de début de location.
     * @param nombreJours Nombre de jours de location.
     * @return La nouvelle location créée.
     * @throws RuntimeException Si client/voiture non trouvés ou voiture non disponible.
     */
    public Location createLocation(String clientCin, String voitureImmat, Utilisateur gestionnaire, LocalDate dateDebut, int nombreJours) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        Location newLocation = new Location();
        try {
            LOGGER.info("Début de la transaction pour createLocation.");
            transaction.begin();

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
                throw new RuntimeException("La voiture " + voitureImmat + " n'est pas disponible pour la location.");
            }
            if (managedGestionnaire == null) {
                throw new RuntimeException("Gestionnaire non trouvé.");
            }

            double montantTotal = voiture.getPrixLocationJ() * nombreJours;
            LocalDate dateRetourPrevue = dateDebut.plusDays(nombreJours);

            newLocation.setClient(client);
            newLocation.setVoiture(voiture);
            newLocation.setUtilisateur(managedGestionnaire);
            newLocation.setDateDebut(dateDebut);
            newLocation.setNombreJours(nombreJours);
            newLocation.setDateRetourPrevue(dateRetourPrevue);
            newLocation.setMontantTotal(montantTotal);
            newLocation.setKilometrageDepart(voiture.getKilometrage());
            newLocation.setStatut("En cours");

            voiture.setStatut("Louee");
            voitureDAO.merge(em, voiture);

            locationDAO.persist(em, newLocation);
            transaction.commit();
            LOGGER.info("Location créée avec succès par gestionnaire pour client " + clientCin + " et voiture " + voitureImmat);
            return newLocation;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
                LOGGER.severe("Transaction createLocation ROLLED BACK: " + e.getMessage());
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
                LOGGER.info("EntityManager fermé après createLocation.");
            }
        }
    }


    /**
     * Enregistre le retour d'une voiture.
     * Met à jour la location (date de retour réelle, kilométrage de retour, statut 'Terminee').
     * Met à jour le statut et le kilométrage de la voiture ('Disponible').
     * @param locationId L'ID de la location à terminer.
     * @param kilometrageRetour Le kilométrage enregistré au retour.
     * @throws RuntimeException Si la location ou la voiture n'est pas trouvée, ou si la location n'est pas 'En cours'.
     */
    public void recordCarReturn(Long locationId, double kilometrageRetour) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            LOGGER.info("Début de la transaction pour recordCarReturn pour location ID: " + locationId);
            transaction.begin();

            Location location = em.find(Location.class, locationId);
            if (location == null) {
                throw new RuntimeException("Location non trouvée.");
            }
            if (!"En cours".equals(location.getStatut())) {
                throw new RuntimeException("Cette location n'est pas en cours et ne peut pas être terminée.");
            }

            Voiture voiture = em.find(Voiture.class, location.getVoiture().getImmatriculation());
            if (voiture == null) {
                throw new RuntimeException("Voiture associée à la location non trouvée.");
            }

            location.setDateRetourReelle(LocalDate.now());
            location.setKilometrageRetour(kilometrageRetour);
            location.setStatut("Terminee");
            locationDAO.merge(em, location);
            LOGGER.info("Location ID " + location.getId() + " statut mis à jour à 'Terminee'.");

            voiture.setKilometrage(kilometrageRetour);
            voiture.setStatut("Disponible");
            voitureDAO.merge(em, voiture);
            LOGGER.info("Voiture " + voiture.getImmatriculation() + " statut mis à jour à 'Disponible' et kilométrage mis à jour.");

            transaction.commit();
            LOGGER.info("Retour de voiture enregistré avec succès pour location ID: " + locationId);

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
                LOGGER.info("EntityManager fermé après recordCarReturn transaction.");
            }
        }
    }

    /**
     * Ajoute une nouvelle demande de location par un client.
     * Cette méthode ne marque PAS la voiture comme "Louée" immédiatement.
     * Le statut initial est "En attente". L'utilisateur (gestionnaire) est laissé à null.
     * @param demande La demande de location à enregistrer.
     * @return La demande de location persistée.
     * @throws RuntimeException Si la voiture n'est pas disponible ou toute autre erreur de persistance.
     */
    public Location addRentalRequest(Location demande) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            LOGGER.info("Début de la transaction pour addRentalRequest.");
            transaction.begin();

            Client managedClient = em.find(Client.class, demande.getClient().getCin());
            Voiture managedVoiture = em.find(Voiture.class, demande.getVoiture().getImmatriculation());

            if (managedClient == null) {
                throw new RuntimeException("Client non trouvé pour la demande.");
            }
            if (managedVoiture == null) {
                throw new RuntimeException("Voiture non trouvée pour la demande.");
            }
            if (!"Disponible".equals(managedVoiture.getStatut())) {
                throw new RuntimeException("La voiture n'est plus disponible pour la location.");
            }

            demande.setClient(managedClient);
            demande.setVoiture(managedVoiture);
            demande.setUtilisateur(null);
            demande.setStatut("En attente");

            locationDAO.persist(em, demande);
            transaction.commit();
            LOGGER.info("Demande de location soumise avec succès par le client " + demande.getClient().getCin() + " pour voiture " + demande.getVoiture().getImmatriculation());
            return demande;
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
                LOGGER.severe("Transaction addRentalRequest ROLLED BACK: " + e.getMessage());
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
                LOGGER.info("EntityManager fermé après addRentalRequest.");
            }
        }
    }

    /**
     * Récupère toutes les locations (incluant les demandes en attente) d'un client spécifique.
     * Utilise JOIN FETCH pour charger Client et Voiture en une seule requête.
     * @param clientCin Le CIN du client.
     * @return Une liste de locations associées à ce client.
     */
    public List<Location> getLocationsByClient(String clientCin) {
        LOGGER.info("Récupération des locations pour le client CIN: " + clientCin);
        EntityManager em = JPAUtil.getEntityManager();
        List<Location> clientLocations = null;
        try {
            TypedQuery<Location> query = em.createQuery(
                "SELECT l FROM Location l JOIN FETCH l.client JOIN FETCH l.voiture LEFT JOIN FETCH l.utilisateur WHERE l.client.cin = :clientCin ORDER BY l.dateDebut DESC", Location.class);
            query.setParameter("clientCin", clientCin);
            clientLocations = query.getResultList();
            LOGGER.info("Nombre de locations récupérées pour le client " + clientCin + ": " + (clientLocations != null ? clientLocations.size() : "0"));
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des locations pour le client " + clientCin + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la récupération des locations du client.", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                LOGGER.info("EntityManager fermé après getLocationsByClient.");
            }
        }
        return clientLocations;
    }

    /**
     * Annule une demande de location (la met en statut 'Annulee').
     * Cette opération n'affecte pas le statut de la voiture car elle n'a pas été louée.
     * @param locationId L'ID de la demande de location à annuler.
     * @throws RuntimeException Si la demande n'est pas trouvée ou n'est pas 'En attente'.
     */
    public void cancelRentalRequest(Long locationId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            LOGGER.info("Début de la transaction pour cancelRentalRequest pour location ID: " + locationId);
            transaction.begin();

            Location location = em.find(Location.class, locationId);
            if (location == null) {
                throw new RuntimeException("Demande de location non trouvée.");
            }
            if (!"En attente".equals(location.getStatut())) {
                throw new RuntimeException("Impossible d'annuler cette demande, son statut n'est pas 'En attente'.");
            }

            location.setStatut("Annulee");
            locationDAO.merge(em, location);
            transaction.commit();
            LOGGER.info("Demande de location ID " + locationId + " annulée avec succès.");
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
                LOGGER.severe("Transaction cancelRentalRequest ROLLED BACK pour la location: " + locationId + " - " + e.getMessage());
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
                LOGGER.info("EntityManager fermé après cancelRentalRequest.");
            }
        }
    }

     /**
     * Recherche les voitures disponibles selon des critères (marque, catégorie).
     * @param marque La marque de la voiture (peut être null pour toutes les marques).
     * @param categorie La catégorie de la voiture (peut être null pour toutes les catégories).
     * @return Une liste de voitures disponibles correspondant aux critères.
     */
    public List<Voiture> searchAvailableCars(String marque, String categorie) {
        LOGGER.info("Recherche de voitures disponibles par marque='" + marque + "' et catégorie='" + categorie + "'.");
        EntityManager em = JPAUtil.getEntityManager();
        List<Voiture> voitures = null;
        try {
            StringBuilder jpql = new StringBuilder("SELECT v FROM Voiture v WHERE v.statut = 'Disponible'");
            if (marque != null && !marque.trim().isEmpty()) {
                jpql.append(" AND v.marque LIKE :marque");
            }
            if (categorie != null && !categorie.trim().isEmpty()) {
                jpql.append(" AND v.categorie LIKE :categorie");
            }

            TypedQuery<Voiture> query = em.createQuery(jpql.toString(), Voiture.class);

            if (marque != null && !marque.trim().isEmpty()) {
                query.setParameter("marque", "%" + marque + "%");
            }
            if (categorie != null && !categorie.trim().isEmpty()) {
                query.setParameter("categorie", "%" + categorie + "%");
            }

            voitures = query.getResultList();
            LOGGER.info("Nombre de voitures disponibles trouvées: " + (voitures != null ? voitures.size() : "0"));
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la recherche de voitures disponibles: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la recherche de voitures disponibles.", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                LOGGER.info("EntityManager fermé après searchAvailableCars.");
            }
        }
        return voitures;
    }

    /**
     * Accepte une demande de location. Change le statut en 'En cours' et assigne le gestionnaire.
     * Met également à jour le statut de la voiture à 'Louee'.
     * @param locationId L'ID de la demande de location à accepter.
     * @param gestionnaire L'utilisateur (gestionnaire) qui valide la demande.
     * @throws RuntimeException Si la demande n'est pas trouvée, n'est pas 'En attente', ou si la voiture n'est plus disponible.
     */
    public void acceptLocationRequest(Long locationId, Utilisateur gestionnaire) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            LOGGER.info("Début de la transaction pour acceptLocationRequest pour location ID: " + locationId);
            transaction.begin();

            Location location = em.find(Location.class, locationId);
            if (location == null) {
                throw new RuntimeException("Demande de location non trouvée.");
            }
            if (!"En attente".equals(location.getStatut())) {
                throw new RuntimeException("Impossible d'accepter cette demande, son statut n'est pas 'En attente'.");
            }

            Voiture voiture = em.find(Voiture.class, location.getVoiture().getImmatriculation());
            if (voiture == null) {
                throw new RuntimeException("Voiture associée à la demande non trouvée.");
            }
            if (!"Disponible".equals(voiture.getStatut())) {
                throw new RuntimeException("La voiture n'est plus disponible pour la location.");
            }

            Utilisateur managedGestionnaire = em.find(Utilisateur.class, gestionnaire.getId());
            if (managedGestionnaire == null) {
                throw new RuntimeException("Gestionnaire non trouvé.");
            }

            location.setStatut("En cours");
            location.setUtilisateur(managedGestionnaire); // Assigne le gestionnaire qui a validé
            locationDAO.merge(em, location);

            voiture.setStatut("Louee"); // La voiture est maintenant louée
            voitureDAO.merge(em, voiture);

            transaction.commit();
            LOGGER.info("Demande de location ID " + locationId + " acceptée par gestionnaire " + gestionnaire.getUsername() + ".");
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
                LOGGER.severe("Transaction acceptLocationRequest ROLLED BACK pour la location: " + locationId + " - " + e.getMessage());
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
                LOGGER.info("EntityManager fermé après acceptLocationRequest.");
            }
        }
    }

    /**
     * Décline une demande de location. Change le statut en 'Annulee'.
     * Le statut de la voiture n'est pas affecté car elle n'a jamais été 'Louee'.
     * @param locationId L'ID de la demande de location à décliner.
     * @throws RuntimeException Si la demande n'est pas trouvée ou n'est pas 'En attente'.
     */
    public void declineLocationRequest(Long locationId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            LOGGER.info("Début de la transaction pour declineLocationRequest pour location ID: " + locationId);
            transaction.begin();

            Location location = em.find(Location.class, locationId);
            if (location == null) {
                throw new RuntimeException("Demande de location non trouvée.");
            }
            if (!"En attente".equals(location.getStatut())) {
                throw new RuntimeException("Impossible de décliner cette demande, son statut n'est pas 'En attente'.");
            }

            location.setStatut("Annulee");
            // L'utilisateur reste null car la demande n'est pas acceptée par un gestionnaire
            locationDAO.merge(em, location);
            transaction.commit();
            LOGGER.info("Demande de location ID " + locationId + " déclinée avec succès.");
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
                LOGGER.severe("Transaction declineLocationRequest ROLLED BACK pour la location: " + locationId + " - " + e.getMessage());
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
                LOGGER.info("EntityManager fermé après declineLocationRequest.");
            }
        }
    }
}
