package com.agence.location.service;

import com.agence.location.dao.JPAUtil; // Importez JPAUtil pour obtenir l'EntityManager
import com.agence.location.dao.UtilisateurDAO;
import com.agence.location.model.Manager;
import com.agence.location.model.Utilisateur;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.logging.Level;

/**
 * Classe de service pour gérer les opérations CRUD sur les gestionnaires.
 * Cette classe utilise UtilisateurDAO pour persister les données des gestionnaires
 * dans la table 'Utilisateur' de la base de données.
 * Elle gère désormais les transactions JPA pour les opérations d'écriture.
 */
public class ManagerDataStore {
    private static final Logger LOGGER = Logger.getLogger(ManagerDataStore.class.getName());
    private UtilisateurDAO utilisateurDAO;

    // Constructeur pour injecter UtilisateurDAO
    public ManagerDataStore() {
        this.utilisateurDAO = new UtilisateurDAO();
        LOGGER.info("ManagerDataStore initialisé avec UtilisateurDAO.");
    }

    /**
     * Ajoute un nouveau gestionnaire à la base de données en tant qu'Utilisateur.
     * Le rôle est automatiquement défini sur "Gestionnaire".
     * @param managerPojo Le POJO Manager contenant les informations du gestionnaire à ajouter.
     * @return L'objet Manager ajouté (avec l'ID généré par la DB), ou null si échec.
     */
    public Manager addManager(Manager managerPojo) {
        EntityManager em = JPAUtil.getEntityManager(); // Obtient un EntityManager
        EntityTransaction transaction = null;
        try {
            transaction = em.getTransaction();
            transaction.begin();

            // Convertir le Manager POJO en Utilisateur entité
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setUsername(managerPojo.getUsername());
            utilisateur.setPassword(managerPojo.getPassword()); // Rappel: hacher le mot de passe en prod !
            utilisateur.setNom(managerPojo.getNom());
            utilisateur.setPrenom(managerPojo.getPrenom());
            utilisateur.setDateRecrutement(managerPojo.getDateRecrutement());
            utilisateur.setEmail(managerPojo.getEmail());
            utilisateur.setTelephone(managerPojo.getTelephone());
            utilisateur.setAdresse(managerPojo.getAdresse());
            utilisateur.setRole("Gestionnaire"); // Assurez-vous que le rôle est correct

            // Utilise la méthode persist du GenericDAO via UtilisateurDAO
            utilisateurDAO.persist(em, utilisateur); // Correction ici : appel à persist avec em

            transaction.commit();
            LOGGER.info("Gestionnaire (Utilisateur) ajouté à la base de données: " + utilisateur.getUsername());
            // Retourner le Manager POJO avec l'ID généré si nécessaire
            managerPojo.setId(String.valueOf(utilisateur.getId())); // Mettre à jour l'ID du POJO
            return managerPojo;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ajout du gestionnaire (Utilisateur) à la base de données.", e);
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close(); // Ferme l'EntityManager
            }
        }
    }

    /**
     * Récupère tous les gestionnaires stockés dans la base de données.
     * @return Une liste de tous les gestionnaires (convertis en POJO Manager).
     */
    public List<Manager> getAllManagers() {
        // Cette méthode utilise findByRole qui gère son propre EntityManager
        List<Utilisateur> utilisateurs = utilisateurDAO.findByRole("Gestionnaire");
        LOGGER.info("Récupération de tous les gestionnaires. Nombre: " + (utilisateurs != null ? utilisateurs.size() : 0));
        // Convertir les entités Utilisateur en POJO Manager
        return utilisateurs.stream()
                .map(u -> new Manager(
                        String.valueOf(u.getId()),
                        u.getUsername(),
                        u.getPassword(), // Attention: ne pas exposer le mot de passe en clair en prod
                        u.getNom(),
                        u.getPrenom(),
                        u.getDateRecrutement(),
                        u.getEmail(),
                        u.getTelephone(),
                        u.getAdresse(),
                        u.getRole()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Trouve un gestionnaire par son nom d'utilisateur.
     * @param username Le nom d'utilisateur du gestionnaire.
     * @return Le gestionnaire trouvé (converti en POJO Manager), ou null si non trouvé.
     */
    public Manager getManagerByUsername(String username) {
        // Cette méthode utilise findByUsername qui gère son propre EntityManager
        Utilisateur utilisateur = utilisateurDAO.findByUsername(username);
        if (utilisateur != null && "Gestionnaire".equals(utilisateur.getRole())) {
            return new Manager(
                    String.valueOf(utilisateur.getId()),
                    utilisateur.getUsername(),
                    utilisateur.getPassword(),
                    utilisateur.getNom(),
                    utilisateur.getPrenom(),
                    utilisateur.getDateRecrutement(),
                    utilisateur.getEmail(),
                    utilisateur.getTelephone(),
                    utilisateur.getAdresse(),
                    utilisateur.getRole()
            );
        }
        return null;
    }

    /**
     * Supprime un gestionnaire par son ID.
     * @param id L'ID du gestionnaire à supprimer.
     * @return true si le gestionnaire a été supprimé, false sinon.
     */
    public boolean deleteManager(String id) {
        EntityManager em = JPAUtil.getEntityManager(); // Obtient un EntityManager
        EntityTransaction transaction = null;
        try {
            Long managerId = Long.parseLong(id);
            transaction = em.getTransaction();
            transaction.begin();

            // Récupère l'entité managée avant de la supprimer
            Utilisateur utilisateurToDelete = em.find(Utilisateur.class, managerId);
            if (utilisateurToDelete != null) {
                utilisateurDAO.remove(em, utilisateurToDelete); // Correction ici : appel à remove avec em
                transaction.commit();
                LOGGER.info("Gestionnaire avec ID " + id + " supprimé avec succès.");
                return true;
            } else {
                LOGGER.warning("Tentative de suppression d'un gestionnaire inexistant avec ID: " + id);
                return false;
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID de gestionnaire invalide pour la suppression: " + id, e);
            return false;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Erreur lors de la suppression du gestionnaire avec ID: " + id, e);
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close(); // Ferme l'EntityManager
            }
        }
    }
}
