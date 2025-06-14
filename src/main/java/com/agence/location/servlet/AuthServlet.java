package com.agence.location.servlet;

import com.agence.location.dao.UtilisateurDAO;
import com.agence.location.model.Utilisateur;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
/**
 * Servlet pour gérer l'authentification des utilisateurs (connexion et déconnexion).
 */
@WebServlet("/auth") // Mappe cette servlet à l'URL /auth
public class AuthServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UtilisateurDAO utilisateurDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        // Initialisation du DAO lors du démarrage de la servlet
        utilisateurDAO = new UtilisateurDAO();
    }

    /**
     * Gère les requêtes GET pour afficher la page de connexion ou déconnecter l'utilisateur.
     * @param request L'objet HttpServletRequest.
     * @param response L'objet HttpServletResponse.
     * @throws ServletException Si une erreur de servlet survient.
     * @throws IOException Si une erreur d'entrée/sortie survient.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("logout".equals(action)) {
            // Invalide la session actuelle pour déconnecter l'utilisateur
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            // Redirige vers la page de connexion
            response.sendRedirect("login.jsp");
        } else {
            // Affiche la page de connexion par défaut
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    /**
     * Gère les requêtes POST pour soumettre les informations de connexion.
     * @param request L'objet HttpServletRequest.
     * @param response L'objet HttpServletResponse.
     * @throws ServletException Si une erreur de servlet survient.
     * @throws IOException Si une erreur d'entrée/sortie survient.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Recherche l'utilisateur dans la base de données
        Utilisateur utilisateur = utilisateurDAO.findByUsername(username);

        // Vérification des identifiants (simple pour l'exemple, à améliorer avec un hachage de mot de passe en production)
        if (utilisateur != null && utilisateur.getPassword().equals(password)) {
            // Si les identifiants sont corrects, crée ou récupère la session
            HttpSession session = request.getSession();
            session.setAttribute("utilisateur", utilisateur); // Stocke l'objet utilisateur dans la session
            session.setAttribute("role", utilisateur.getRole()); // Stocke le rôle de l'utilisateur

            // Redirige l'utilisateur vers son tableau de bord respectif
            if ("ChefAgence".equals(utilisateur.getRole())) {
                response.sendRedirect("dashboard?role=chef");
            } else if ("Gestionnaire".equals(utilisateur.getRole())) {
                response.sendRedirect("dashboard?role=gestionnaire");
            } else {
                // Rôle non reconnu, affiche un message d'erreur
                request.setAttribute("error", "Rôle d'utilisateur inconnu.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } else {
            // Identifiants incorrects, affiche un message d'erreur
            request.setAttribute("error", "Nom d'utilisateur ou mot de passe incorrect.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
    public void destroy() {
        // Ferme l'EntityManagerFactory lorsque la servlet est détruite
        UtilisateurDAO.closeEntityManagerFactory();
        super.destroy();
    }
}
