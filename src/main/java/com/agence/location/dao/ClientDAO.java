package com.agence.location.dao;

import com.agence.location.model.Client;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * DAO spécifique pour l'entité Client.
 * Étend GenericDAO pour hériter des opérations CRUD de base.
 * Les opérations de modification sont gérées par la couche Service via les méthodes de GenericDAO
 * qui nécessitent un EntityManager managé.
 */
public class ClientDAO extends GenericDAO<Client, String> {

    private static final Logger LOGGER = Logger.getLogger(ClientDAO.class.getName());

    /**
     * Constructeur par défaut.
     */
    public ClientDAO() {
        super(Client.class); // Indique à GenericDAO que ce DAO gère l'entité Client
    }

    /**
     * Recherche un client par son nom.
     * Cette méthode de lecture obtient et ferme son propre EntityManager.
     * @param nom Le nom du client à rechercher.
     * @return Le client trouvé, ou null si aucun client avec ce nom n'est trouvé.
     */
    public Client findByNom(String nom) {
        EntityManager em = JPAUtil.getEntityManager(); // Obtient un EntityManager depuis JPAUtil
        Client client = null;
        try {
            // Crée une requête JPQL pour trouver un client par son nom.
            TypedQuery<Client> query = em.createQuery("SELECT c FROM Client c WHERE c.nom = :nom", Client.class);
            query.setParameter("nom", nom); // Définit le paramètre de la requête
            client = query.getSingleResult(); // Exécute la requête et retourne un seul résultat
        } catch (NoResultException e) {
            LOGGER.info("Aucun client trouvé avec le nom: " + nom);
            // Aucun résultat, retourne null
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche du client par nom: " + e.getMessage(), e);
            // Gérer d'autres exceptions, par exemple, logguer l'erreur
        } finally {
            if (em != null && em.isOpen()) {
                em.close(); // Ferme l'EntityManager
            }
        }
        return client;
    }

    /**
     * Recherche un client par son CIN et son mot de passe.
     * Cette méthode de lecture obtient et ferme son propre EntityManager.
     * @param cin Le CIN du client à rechercher.
     * @param password Le mot de passe du client.
     * @return Le client trouvé, ou null si aucun client avec ce CIN et mot de passe n'est trouvé.
     */
    public Client findByCinAndPassword(String cin, String password) {
        EntityManager em = JPAUtil.getEntityManager();
        Client client = null;
        try {
            TypedQuery<Client> query = em.createQuery(
                "SELECT c FROM Client c WHERE c.cin = :cin AND c.password = :password", Client.class);
            query.setParameter("cin", cin);
            query.setParameter("password", password);
            client = query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.info("Aucun client trouvé avec le CIN: " + cin + " et le mot de passe fourni.");
            // Aucun résultat, retourne null
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche du client par CIN et mot de passe: " + e.getMessage(), e);
            // Gérer d'autres exceptions
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return client;
    }

    // Les méthodes comme save(), delete() etc. ne sont plus définies ici,
    // car la couche Service appellera persist(), merge(), remove() de GenericDAO
    // en lui passant l'EntityManager pour la gestion transactionnelle.
}
