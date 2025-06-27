package com.agence.location.servlet;

import com.agence.location.service.AuthService;
import com.agence.location.model.Utilisateur;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet pour gérer l'authentification des utilisateurs (personnel de l'agence).
 * Gère la connexion et la déconnexion.
 */
@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AuthServlet.class.getName());

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        super.init();
        authService = new AuthService();
        LOGGER.info("AuthServlet initialisée.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("logout".equals(action)) {
            // Gère la déconnexion du personnel.
            HttpSession session = request.getSession(false); // Ne crée pas de nouvelle session si elle n'existe pas
            if (session != null) {
                session.invalidate(); // Invalide la session actuelle
                LOGGER.info("Utilisateur déconnecté, session invalidée.");
            }
            // Redirige l'utilisateur vers la page de connexion, en utilisant le chemin de contexte
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        } else {
            // Par défaut, affiche la page de connexion si aucune action spécifique n'est demandée.
            LOGGER.info("Accès à la page de connexion.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Utilisateur utilisateur = authService.authenticate(username, password);

            if (utilisateur != null) {
                HttpSession session = request.getSession(); // Récupère ou crée une session
                session.setAttribute("utilisateur", utilisateur); // Stocke l'objet utilisateur complet
                session.setAttribute("role", utilisateur.getRole()); // Stocke le rôle (Gestionnaire, ChefAgence)

                LOGGER.info("Utilisateur " + utilisateur.getUsername() + " (" + utilisateur.getRole() + ") authentifié avec succès.");
                // Redirige vers le tableau de bord principal après connexion réussie
                response.sendRedirect(request.getContextPath() + "/dashboard");
            } else {
                LOGGER.warning("Échec de l'authentification pour l'utilisateur: " + username);
                request.setAttribute("error", "Nom d'utilisateur ou mot de passe incorrect.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'authentification de l'utilisateur " + username + ": " + e.getMessage(), e);
            request.setAttribute("error", "Une erreur est survenue lors de la connexion. Veuillez réessayer plus tard.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        LOGGER.info("AuthServlet détruite.");
    }
}
