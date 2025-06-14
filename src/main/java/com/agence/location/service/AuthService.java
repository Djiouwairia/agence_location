package com.agence.location.service;

import com.agence.location.dao.UtilisateurDAO;
import com.agence.location.model.Utilisateur;

/**
 * Service pour la gestion de l'authentification des utilisateurs.
 */
public class AuthService {

    private UtilisateurDAO utilisateurDAO;

    public AuthService() {
        this.utilisateurDAO = new UtilisateurDAO();
    }

    /**
     * Authentifie un utilisateur.
     * @param username Le nom d'utilisateur.
     * @param password Le mot de passe.
     * @return L'objet Utilisateur si l'authentification réussit, sinon null.
     */
    public Utilisateur authenticate(String username, String password) {
        Utilisateur utilisateur = utilisateurDAO.findByUsername(username);
        // Dans une application réelle, le mot de passe doit être haché et vérifié de manière sécurisée (ex: BCrypt)
        if (utilisateur != null && utilisateur.getPassword().equals(password)) {
            return utilisateur;
        }
        return null;
    }
}
