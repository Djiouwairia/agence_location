package com.agence.location.dao;

import com.agence.location.model.Client;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 * DAO spécifique pour l'entité Client.
 * Étend GenericDAO pour hériter des opérations CRUD de base.
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
     * @param nom Le nom du client à rechercher.
     * @return Le client trouvé, ou null si aucun client avec ce nom n'est trouvé.
     */
    public Client findByNom(String nom) {
        em = getEntityManager();
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
                em.close();
            }
        }
        return client;
    }

    // Vous pouvez ajouter d'autres méthodes de recherche spécifiques aux clients ici,
    // comme findByEmail, findByTelephone, etc., si nécessaire pour vos fonctionnalités.
}
