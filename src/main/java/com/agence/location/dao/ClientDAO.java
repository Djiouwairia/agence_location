package com.agence.location.dao;

import com.agence.location.model.Client;
import javax.persistence.EntityManager; // Importez EntityManager
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * DAO spécifique pour l'entité Client.
 * Étend GenericDAO pour hériter des opérations CRUD de base.
 * Les opérations de modification sont gérées par la couche Service via les méthodes de GenericDAO
 * qui nécessitent un EntityManager managé.
 */
public class ClientDAO extends GenericDAO<Client, String> {

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
            query.setParameter("nom", nom); // Définit le paramètre 'nom'
            client = query.getSingleResult(); // Tente de récupérer un unique résultat
        } catch (NoResultException e) {
            // Aucune entité trouvée pour le nom donné, retourne null.
            System.out.println("Aucun client trouvé avec le nom: " + nom);
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche du client par nom: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close(); // Ferme l'EntityManager après utilisation
            }
        }
        return client;
    }

    // Les méthodes comme save(), delete() etc. ne sont plus définies ici,
    // car la couche Service appellera persist(), merge(), remove() de GenericDAO
    // en lui passant l'EntityManager pour la gestion transactionnelle.
}
