package com.agence.location.service;

import com.agence.location.dao.ClientDAO;
import com.agence.location.dao.JPAUtil;
import com.agence.location.model.Client;

import javax.persistence.EntityManager;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service d'authentification pour les clients.
 * Gère la logique métier liée à la connexion des clients.
 */
public class ClientAuthService {

    private static final Logger LOGGER = Logger.getLogger(ClientAuthService.class.getName());
    private ClientDAO clientDAO;

    public ClientAuthService() {
        this.clientDAO = new ClientDAO();
    }

    /**
     * Authentifie un client en utilisant son CIN et son mot de passe.
     * @param cin Le numéro CIN du client.
     * @param password Le mot de passe du client.
     * @return L'objet Client si l'authentification réussit, sinon null.
     */
    public Client authenticate(String cin, String password) {
        LOGGER.info("Tentative d'authentification pour le client avec CIN: " + cin);
        EntityManager em = JPAUtil.getEntityManager(); // Obtient un EntityManager pour cette opération
        try {
            Client client = clientDAO.findByCinAndPassword(cin, password); // Appelle la méthode du DAO
            if (client != null) {
                LOGGER.info("Client " + cin + " authentifié avec succès.");
            } else {
                LOGGER.warning("Échec de l'authentification pour le client avec CIN: " + cin + ". Identifiants incorrects.");
            }
            return client;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'authentification du client avec CIN: " + cin, e);
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close(); // Ferme l'EntityManager
            }
        }
    }
}
