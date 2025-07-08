package com.agence.location.dao;

import com.agence.location.model.Utilisateur;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO spécifique pour l'entité Utilisateur (Gestionnaire ou Chef d'agence).
 * Étend GenericDAO pour hériter des opérations CRUD de base.
 */
public class UtilisateurDAO extends GenericDAO<Utilisateur, Long> {

    private static final Logger LOGGER = Logger.getLogger(UtilisateurDAO.class.getName());

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
        EntityManager em = JPAUtil.getEntityManager();
        Utilisateur utilisateur = null;
        try {
            TypedQuery<Utilisateur> query = em.createQuery("SELECT u FROM Utilisateur u WHERE u.username = :username", Utilisateur.class);
            query.setParameter("username", username);
            utilisateur = query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.info("Aucun utilisateur trouvé avec le nom d'utilisateur: " + username);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche de l'utilisateur par nom d'utilisateur: " + username, e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return utilisateur;
    }

    /**
     * Récupère tous les utilisateurs avec un rôle spécifique.
     * @param role Le rôle à filtrer (par exemple, "Gestionnaire").
     * @return Une liste d'objets Utilisateur correspondant au rôle, ou une liste vide si aucun n'est trouvé.
     */
    public List<Utilisateur> findByRole(String role) {
        EntityManager em = JPAUtil.getEntityManager();
        List<Utilisateur> utilisateurs = null;
        try {
            TypedQuery<Utilisateur> query = em.createQuery("SELECT u FROM Utilisateur u WHERE u.role = :role", Utilisateur.class);
            query.setParameter("role", role);
            utilisateurs = query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la recherche des utilisateurs par rôle: " + role, e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
        return utilisateurs;
    }

    /**
     * Met à jour un utilisateur existant.
     * Cette méthode utilise la méthode merge du GenericDAO.
     * @param em L'EntityManager courant de la transaction.
     * @param utilisateur L'objet Utilisateur à mettre à jour.
     * @return L'objet Utilisateur mis à jour.
     */
    public Utilisateur update(EntityManager em, Utilisateur utilisateur) {
        return merge(em, utilisateur); // Appelle la méthode merge du GenericDAO
    }
}
