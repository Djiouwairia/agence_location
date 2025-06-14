package com.agence.location.servlet;

import com.agence.location.service.AuthService;
import com.agence.location.model.Utilisateur;
import com.agence.location.dao.JPAUtil; // Pour fermer l'EMF à l'arrêt

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
/**
 * Servlet pour gérer l'authentification des utilisateurs (connexion et déconnexion).
 * Délègue la logique d'authentification à AuthService.
 */
@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        super.init();
        authService = new AuthService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect("login.jsp");
        } else {
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Utilisateur utilisateur = authService.authenticate(username, password);

        if (utilisateur != null) {
            HttpSession session = request.getSession();
            session.setAttribute("utilisateur", utilisateur);
            session.setAttribute("role", utilisateur.getRole());

            if ("ChefAgence".equals(utilisateur.getRole())) {
                response.sendRedirect("dashboard?role=chef");
            } else if ("Gestionnaire".equals(utilisateur.getRole())) {
                response.sendRedirect("dashboard?role=gestionnaire");
            } else {
                request.setAttribute("error", "Rôle d'utilisateur inconnu.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("error", "Nom d'utilisateur ou mot de passe incorrect.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    // Le fermeture de l'EntityManagerFactory est maintenant gérée par AppServletContextListener
    // @Override
    // public void destroy() {
    //     JPAUtil.closeEntityManagerFactory();
    //     super.destroy();
    // }
}
