package com.agence.location.service;

import com.agence.location.dao.ClientDAO;
import com.agence.location.model.Client;

/**
 * Service pour la gestion de l'authentification des clients.
 */
public class ClientAuthService {

    private ClientDAO clientDAO;

    public ClientAuthService() {
        this.clientDAO = new ClientDAO();
    }

    /**
     * Authentifie un client.
     * @param cin Le CIN du client.
     * @param password Le mot de passe du client.
     * @return L'objet Client si l'authentification réussit, sinon null.
     */
    public Client authenticate(String cin, String password) {
        // Recherche le client par CIN et mot de passe dans la base de données via le DAO.
        // NOTE IMPORTANTE: Dans une application réelle, le mot de passe ne doit JAMAIS
        // être stocké en clair. Utilisez des techniques de hachage comme BCrypt pour sécuriser les mots de passe.
        // Pour cet exemple, nous comparons directement le mot de passe fourni avec celui stocké.
        Client client = clientDAO.findByCinAndPassword(cin, password);

        // Vérifie si un client a été trouvé et si le mot de passe correspond.
        // Si findByCinAndPassword renvoie déjà null en cas de non-concordance du mot de passe
        // ou de l'utilisateur, cette vérification supplémentaire est redondante mais ne nuit pas.
        // Cependant, l'implémentation actuelle de findByCinAndPassword inclut déjà la vérification du mot de passe.
        if (client != null) {
            return client; // Retourne le client authentifié
        }
        return null; // L'authentification a échoué
    }
}
