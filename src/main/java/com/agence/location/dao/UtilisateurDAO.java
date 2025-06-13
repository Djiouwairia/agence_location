package com.agence.location.dao;

import com.agence.location.model.Utilisateur;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 * DAO spécifique pour l'entité Utilisateur (Gestionnaire ou Chef d'agence).
 * Étend GenericDAO pour hériter des opérations CRUD de base.
 */
public class UtilisateurDAO extends GenericDAO<Utilisateur, Long> {

    /**
     * Constructeur par défaut.
     */
    public UtilisateurDAO() {
        super(Utilisateur.class); // Indique à GenericDAO que ce DAO gère l'entité Utilisateur
    }

    /**
     * Recherche un utilisateur par son nom d'utilisateur.
     * Utilisé principalement pour l'authentification.
     * @param username Le nom d'utilisateur à rechercher.
     * @return L'utilisateur trouvé, ou null si aucun utilisateur avec ce nom d'utilisateur n'est trouvé.
     */
    public Utilisateur findByUsername(String username) {
        em = getEntityManager();
        Utilisateur utilisateur = null;
        try {
            TypedQuery<Utilisateur> query = em.createQuery("SELECT u FROM Utilisateur u WHERE u.username = :username", Utilisateur.class);
            query.setParameter("username", username);
            utilisateur = query.getSingleResult();
        } catch (NoResultException e) {
            // Aucun utilisateur trouvé avec ce nom d'utilisateur, retourne null.
            System.out.println("Aucun utilisateur trouvé avec le nom d'utilisateur: " + username);
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche de l'utilisateur par nom d'utilisateur: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return utilisateur;
    }

    // Vous pouvez ajouter d'autres méthodes de recherche ou de gestion des utilisateurs ici,
    // comme findByRole, etc.
}
