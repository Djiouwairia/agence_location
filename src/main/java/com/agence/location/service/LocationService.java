package com.agence.location.service;

import com.agence.location.dao.JPAUtil;
import com.agence.location.dao.LocationDAO;
import com.agence.location.model.Location;
import com.agence.location.model.Voiture;
import com.agence.location.model.Client;
import com.agence.location.model.Utilisateur;
import com.agence.location.dao.ClientDAO; // Ajouté pour findClientByCin
import com.agence.location.dao.VoitureDAO; // Ajouté pour findVoitureByImmatriculation
import com.agence.location.dao.UtilisateurDAO; // Ajouté pour findUtilisateurById

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service pour la gestion des locations.
 * Gère les transactions et la logique métier spécifique aux locations.
 */
public class LocationService {

    private static final Logger LOGGER = Logger.getLogger(LocationService.class.getName());

    private LocationDAO locationDAO;
    private ClientDAO clientDAO; // Ajouté
    private VoitureDAO voitureDAO; // Ajouté
    private UtilisateurDAO utilisateurDAO; // Ajouté

    public LocationService() {
        this.locationDAO = new LocationDAO();
        this.clientDAO = new ClientDAO(); // Initialisation
        this.voitureDAO = new VoitureDAO(); // Initialisation
        this.utilisateurDAO = new UtilisateurDAO(); // Initialisation
        LOGGER.info("LocationService initialisé.");
    }

    /**
     * Récupère toutes les locations enregistrées avec tous les détails (Client, Voiture, Utilisateur).
     * @return Une liste de toutes les locations.
     */
    public List<Location> getAllLocationsWithDetails() {
        LOGGER.info("Appel de getAllLocationsWithDetails.");
        // Utilise findAllWithDetails du LocationDAO pour charger les relations
        List<Location> locations = locationDAO.findAllWithDetails();
        LOGGER.info("Nombre de locations récupérées avec détails: " + (locations != null ? locations.size() : "0"));
        return locations;
    }

    /**
     * Récupère une location par son ID, incluant les détails du client et de la voiture.
     * @param id L'ID de la location.
     * @return L'objet Location correspondant, ou null si non trouvé.
     */
    public Location getLocationByIdWithDetails(Long id) {
        LOGGER.info("Recherche location par ID avec détails: " + id);
        return locationDAO.findByIdWithDetails(id); // Appel au DAO
    }

    /**
     * Ajoute une nouvelle demande de location par un client.
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

            // S'assurer que le client et la voiture sont managés avant de les associer
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

            // Assurer que les entités associées à la demande sont managées
            demande.setClient(managedClient);
            demande.setVoiture(managedVoiture);
            demande.setUtilisateur(null); // L'utilisateur sera défini lors de la validation par le gestionnaire
            demande.setStatut("En attente"); // Statut initial

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
     * Crée une nouvelle location initiée par le personnel (gestionnaire/chef d'agence).
     * La voiture est immédiatement marquée comme "Louee" et le statut de la location est "En cours".
     *
     * @param clientCin Le CIN du client.
     * @param voitureImmat L'immatriculation de la voiture.
     * @param gestionnaire L'utilisateur (gestionnaire) qui enregistre la location.
     * @param dateDebut La date de début de la location.
     * @param nombreJours Le nombre de jours de location prévus.
     * @return La nouvelle location créée.
     * @throws RuntimeException Si le client, la voiture ou le gestionnaire n'est pas trouvé,
     * ou si la voiture n'est pas disponible.
     */
    public Location createLocation(String clientCin, String voitureImmat, Utilisateur gestionnaire, LocalDate dateDebut, int nombreJours) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            LOGGER.info("Début de la transaction pour createLocation par le personnel.");
            transaction.begin();

            Client client = em.find(Client.class, clientCin);
            Voiture voiture = em.find(Voiture.class, voitureImmat);
            Utilisateur managedGestionnaire = em.find(Utilisateur.class, gestionnaire.getId());

            if (client == null) {
                throw new RuntimeException("Client non trouvé.");
            }
            if (voiture == null) {
                throw new RuntimeException("Voiture non trouvée.");
            }
            if (!"Disponible".equals(voiture.getStatut())) {
                throw new RuntimeException("La voiture n'est pas disponible pour la location.");
            }
            if (managedGestionnaire == null) {
                throw new RuntimeException("Gestionnaire non trouvé.");
            }

            // Mettre à jour le statut de la voiture
            voiture.setStatut("Louee");
            em.merge(voiture); // Fusionne la voiture mise à jour

            // Créer l'objet Location
            Location newLocation = new Location();
            newLocation.setClient(client);
            newLocation.setVoiture(voiture);
            newLocation.setUtilisateur(managedGestionnaire);
            newLocation.setDateDebut(dateDebut);
            newLocation.setNombreJours(nombreJours);
            newLocation.setDateRetourPrevue(dateDebut.plusDays(nombreJours));
            newLocation.setMontantTotal(nombreJours * voiture.getPrixLocationJ());
            newLocation.setKilometrageDepart(voiture.getKilometrage());
            newLocation.setKilometrageRetour(null); // Pas de kilométrage de retour initialement
            newLocation.setStatut("En cours"); // Statut immédiat

            em.persist(newLocation); // Persiste la nouvelle location
            transaction.commit();
            LOGGER.info("Location enregistrée avec succès par " + gestionnaire.getUsername() + " pour client " + clientCin + " et voiture " + voitureImmat);
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
     * Accepte une demande de location et met à jour le statut de la voiture.
     * Renommé de acceptRentalRequest pour cohérence avec LocationServlet.
     *
     * @param locationId L'ID de la demande de location à accepter.
     * @param gestionnaire L'utilisateur (gestionnaire) qui accepte la demande.
     * @throws RuntimeException Si la demande n'est pas trouvée, n'est pas en attente, ou si la voiture n'est pas disponible.
     */
    public void acceptLocationRequest(Long locationId, Utilisateur gestionnaire) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            LOGGER.info("Début de la transaction pour acceptLocationRequest pour location ID: " + locationId);
            transaction.begin();

            Location location = em.find(Location.class, locationId);
            if (location == null) {
                LOGGER.warning("Demande de location ID " + locationId + " non trouvée pour acceptation.");
                throw new RuntimeException("Demande de location non trouvée.");
            }
            if (!"En attente".equals(location.getStatut())) {
                LOGGER.warning("Demande de location ID " + locationId + " n'est pas en statut 'En attente'. Statut actuel: " + location.getStatut());
                throw new RuntimeException("La demande de location n'est pas en attente d'acceptation.");
            }

            Voiture voiture = em.find(Voiture.class, location.getVoiture().getImmatriculation());
            if (voiture == null || !"Disponible".equals(voiture.getStatut())) {
                LOGGER.warning("Voiture " + location.getVoiture().getImmatriculation() + " non disponible pour acceptation de la location ID " + locationId + ". Statut voiture: " + (voiture != null ? voiture.getStatut() : "Non trouvée"));
                throw new RuntimeException("La voiture n'est pas disponible.");
            }

            // Mettre à jour le statut de la location et de la voiture
            location.setStatut("En cours");
            location.setUtilisateur(em.find(Utilisateur.class, gestionnaire.getId())); // Associe le gestionnaire
            voiture.setStatut("Louee"); // Le statut de la voiture passe à "Louee"

            em.merge(location);
            em.merge(voiture); // Fusionne la voiture mise à jour
            transaction.commit();
            LOGGER.info("Demande de location ID " + locationId + " acceptée avec succès. Voiture " + voiture.getImmatriculation() + " est maintenant 'Louee'.");
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
                LOGGER.severe("Transaction acceptLocationRequest ROLLED BACK pour la location ID: " + locationId + " - " + e.getMessage());
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
     * Décline une demande de location.
     * Renommé de declineRentalRequest pour cohérence avec LocationServlet.
     *
     * @param locationId L'ID de la demande de location à décliner.
     * @throws RuntimeException Si la demande n'est pas trouvée ou n'est pas en attente.
     */
    public void declineLocationRequest(Long locationId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            LOGGER.info("Début de la transaction pour declineLocationRequest pour location ID: " + locationId);
            transaction.begin();

            Location location = em.find(Location.class, locationId);
            if (location == null) {
                LOGGER.warning("Demande de location ID " + locationId + " non trouvée pour déclinaison.");
                throw new RuntimeException("Demande de location non trouvée.");
            }
            if (!"En attente".equals(location.getStatut())) {
                LOGGER.warning("Demande de location ID " + locationId + " n'est pas en statut 'En attente'. Statut actuel: " + location.getStatut());
                throw new RuntimeException("La demande de location n'est pas en attente de déclinaison.");
            }

            location.setStatut("Annulee"); // Le statut passe à "Annulee"

            em.merge(location);
            // La voiture reste "Disponible" car elle n'a jamais été louée
            transaction.commit();
            LOGGER.info("Demande de location ID " + locationId + " déclinée avec succès.");
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
                LOGGER.severe("Transaction declineLocationRequest ROLLED BACK pour la location ID: " + locationId + " - " + e.getMessage());
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
                LOGGER.info("EntityManager fermé après declineLocationRequest.");
            }
        }
    }


    /**
     * Enregistre le retour d'une voiture et clôture la location.
     * Renommé de returnCar pour cohérence avec LocationServlet.
     *
     * @param locationId L'ID de la location à clôturer.
     * @param kilometrageRetour Le kilométrage enregistré au retour.
     * @throws RuntimeException Si la location n'est pas trouvée, n'est pas en cours, ou si le kilométrage est invalide.
     */
    public void recordCarReturn(Long locationId, double kilometrageRetour) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            LOGGER.info("Début de la transaction pour recordCarReturn pour location ID: " + locationId);
            transaction.begin();

            Location location = em.find(Location.class, locationId);
            if (location == null) {
                LOGGER.warning("Location ID " + locationId + " non trouvée pour le retour.");
                throw new RuntimeException("Location non trouvée.");
            }
            if (!"En cours".equals(location.getStatut())) {
                LOGGER.warning("Location ID " + locationId + " n'est pas en statut 'En cours'. Statut actuel: " + location.getStatut());
                throw new RuntimeException("Cette location n'est pas en cours.");
            }

            // Nous utilisons LocalDate.now() pour la date de retour réelle si elle n'est pas passée explicitement.
            // Si le formulaire du JSP fournit 'dateRetourReelle', LocationServlet doit le parser et le passer.
            // Pour l'instant, je m'adapte à ce que LocationServlet.doPost envoie (uniquement kilometrageRetour).
            LocalDate dateRetourReelle = LocalDate.now();

            // Vérifier que le kilométrage de retour est supérieur au kilométrage de départ
            if (kilometrageRetour < location.getKilometrageDepart()) {
                LOGGER.warning("Kilométrage de retour (" + kilometrageRetour + ") inférieur au kilométrage de départ (" + location.getKilometrageDepart() + ") pour location ID " + locationId);
                throw new RuntimeException("Le kilométrage de retour ne peut pas être inférieur au kilométrage de départ.");
            }

            // Calculer le montant final si des pénalités sont appliquées ou des jours supplémentaires
            double finalMontantTotal = location.getMontantTotal();
            long joursSupp = 0;
            // Si la date de retour réelle est après la date de retour prévue
            if (dateRetourReelle.isAfter(location.getDateRetourPrevue())) {
                joursSupp = java.time.temporal.ChronoUnit.DAYS.between(location.getDateRetourPrevue(), dateRetourReelle);
                if (joursSupp > 0) {
                    // Supposons que le prix par jour est toujours le même
                    finalMontantTotal += joursSupp * location.getVoiture().getPrixLocationJ();
                    LOGGER.info("Jours supplémentaires détectés pour location ID " + locationId + ": " + joursSupp + " jours. Montant ajusté à " + finalMontantTotal);
                }
            }


            location.setStatut("Terminee");
            location.setDateRetourReelle(dateRetourReelle); // Définir la date de retour réelle
            location.setKilometrageRetour(kilometrageRetour);
            location.setMontantTotal(finalMontantTotal); // Mettre à jour le montant total final

            // Mettre à jour le statut de la voiture à "Disponible" et son nouveau kilométrage
            Voiture voiture = em.find(Voiture.class, location.getVoiture().getImmatriculation());
            if (voiture != null) {
                voiture.setStatut("Disponible");
                voiture.setKilometrage(kilometrageRetour); // Met à jour le kilométrage de la voiture
                em.merge(voiture); // Fusionne la voiture mise à jour
            } else {
                LOGGER.warning("Voiture non trouvée lors du retour pour immatriculation: " + location.getVoiture().getImmatriculation());
            }

            em.merge(location); // Fusionne la location mise à jour
            transaction.commit();
            LOGGER.info("Location ID " + locationId + " clôturée avec succès. Voiture " + (voiture != null ? voiture.getImmatriculation() : "N/A") + " est maintenant 'Disponible'.");
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
                LOGGER.severe("Transaction recordCarReturn ROLLED BACK pour la location ID: " + locationId + " - " + e.getMessage());
            }
            throw e;
        } finally {
            if (em.isOpen()) {
                em.close();
                LOGGER.info("EntityManager fermé après recordCarReturn.");
            }
        }
    }


    /**
     * Annule une demande de location initiée par le client (statut 'En attente').
     * @param locationId L'ID de la location à annuler.
     * @throws RuntimeException Si la location n'est pas trouvée ou n'est pas en attente.
     */
    public void cancelRentalRequest(Long locationId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            LOGGER.info("Début de la transaction pour cancelRentalRequest pour location ID: " + locationId);
            transaction.begin();

            Location location = em.find(Location.class, locationId);
            if (location == null) {
                LOGGER.warning("Location ID " + locationId + " non trouvée pour annulation.");
                throw new RuntimeException("Location non trouvée.");
            }
            if (!"En attente".equals(location.getStatut())) {
                LOGGER.warning("Location ID " + locationId + " n'est pas en statut 'En attente'. Statut actuel: " + location.getStatut());
                throw new RuntimeException("Cette location ne peut pas être annulée car elle n'est pas en attente.");
            }

            location.setStatut("Annulee"); // Met à jour le statut à "Annulee"
            em.merge(location); // Fusionne la location mise à jour

            // La voiture n'a pas été affectée, donc son statut reste inchangé ("Disponible")

            transaction.commit();
            LOGGER.info("Location ID " + locationId + " annulée avec succès par le client.");
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
                LOGGER.severe("Transaction cancelRentalRequest ROLLED BACK pour la location ID: " + locationId + " - " + e.getMessage());
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
     * Récupère toutes les locations associées à un client spécifique.
     * @param clientCin Le CIN du client.
     * @return Une liste des locations du client.
     */
    public List<Location> getLocationsByClient(String clientCin) {
        EntityManager em = JPAUtil.getEntityManager();
        List<Location> clientLocations = null;
        try {
            // Utilise JOIN FETCH pour charger le client et la voiture
            TypedQuery<Location> query = em.createQuery(
                "SELECT l FROM Location l JOIN FETCH l.client JOIN FETCH l.voiture LEFT JOIN FETCH l.utilisateur WHERE l.client.cin = :clientCin ORDER BY l.id DESC", Location.class);
            query.setParameter("clientCin", clientCin);
            clientLocations = query.getResultList();
            LOGGER.info("Nombre de locations récupérées pour le client " + clientCin + ": " + (clientLocations != null ? clientLocations.size() : "0"));
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des locations pour le client " + clientCin + ": " + e.getMessage());
            return new ArrayList<>(); // Retourne une liste vide en cas d'erreur
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return clientLocations;
    }

    /**
     * Compte le nombre total de locations (tous statuts) pour un client donné.
     * @param clientCin Le CIN du client.
     * @return Le nombre total de locations du client.
     */
    public int getClientRentalsCount(String clientCin) {
        EntityManager em = JPAUtil.getEntityManager();
        int count = 0;
        try {
            Long result = em.createQuery("SELECT COUNT(l) FROM Location l WHERE l.client.cin = :clientCin", Long.class)
                            .setParameter("clientCin", clientCin)
                            .getSingleResult();
            count = result.intValue();
            LOGGER.info("Nombre de locations pour le client " + clientCin + ": " + count);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors du comptage des locations pour le client " + clientCin + ": " + e.getMessage());
            throw new RuntimeException("Erreur lors du comptage des locations du client.", e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return count;
    }

    /**
     * Récupère les N dernières locations d'un client.
     * Utilisée pour afficher les locations récentes sur le tableau de bord client.
     * @param clientCin Le CIN du client.
     * @param limit Le nombre maximum de locations à récupérer.
     * @return Une liste des locations récentes du client.
     */
    public List<Location> getRecentLocationsByClient(String clientCin, int limit) {
        EntityManager em = JPAUtil.getEntityManager();
        List<Location> recentLocations = null;
        try {
            TypedQuery<Location> query = em.createQuery(
                "SELECT l FROM Location l JOIN FETCH l.client JOIN FETCH l.voiture WHERE l.client.cin = :clientCin ORDER BY l.id DESC", Location.class);
            query.setParameter("clientCin", clientCin);
            query.setMaxResults(limit); // Limite le nombre de résultats
            recentLocations = query.getResultList();
            LOGGER.info("Nombre de locations récentes pour le client " + clientCin + ": " + (recentLocations != null ? recentLocations.size() : "0"));
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la récupération des locations récentes pour le client " + clientCin + ": " + e.getMessage());
            return new ArrayList<>(); // Retourne une liste vide en cas d'erreur
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return recentLocations;
    }

    /**
     * Recherche les voitures disponibles selon des critères de marque et catégorie.
     * Cette méthode devrait être dans VoitureService idéalement, mais est placée ici
     * temporairement pour résoudre les dépendances de compilation de LocationServlet.
     * @param marque La marque de la voiture (peut être null ou vide pour ignorer).
     * @param categorie La catégorie de la voiture (peut être null ou vide pour ignorer).
     * @return Une liste de voitures disponibles correspondant aux critères.
     */
    public List<Voiture> searchAvailableCars(String marque, String categorie) {
        EntityManager em = JPAUtil.getEntityManager();
        List<Voiture> availableCars = new ArrayList<>();
        try {
            // Requête dynamique pour filtrer les voitures "Disponible" par marque et/ou catégorie
            StringBuilder jpql = new StringBuilder("SELECT v FROM Voiture v WHERE v.statut = 'Disponible'");

            if (marque != null && !marque.trim().isEmpty()) {
                jpql.append(" AND LOWER(v.marque) LIKE :marque");
            }
            if (categorie != null && !categorie.trim().isEmpty()) {
                jpql.append(" AND LOWER(v.categorie) LIKE :categorie");
            }

            TypedQuery<Voiture> query = em.createQuery(jpql.toString(), Voiture.class);

            if (marque != null && !marque.trim().isEmpty()) {
                query.setParameter("marque", "%" + marque.toLowerCase() + "%");
            }
            if (categorie != null && !categorie.trim().isEmpty()) {
                query.setParameter("categorie", "%" + categorie.toLowerCase() + "%");
            }

            availableCars = query.getResultList();
            LOGGER.info("searchAvailableCars: " + (availableCars != null ? availableCars.size() : "0") + " voitures disponibles trouvées.");
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la recherche des voitures disponibles: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return availableCars;
    }
}
