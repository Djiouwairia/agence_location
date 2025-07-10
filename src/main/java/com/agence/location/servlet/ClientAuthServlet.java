package com.agence.location.servlet;

import com.agence.location.service.ClientAuthService;
import com.agence.location.model.Client;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Servlet pour gérer l'authentification des clients (connexion et déconnexion).
 * Délègue la logique d'authentification à ClientAuthService.
 */
@WebServlet("/clientAuth")
public class ClientAuthServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ClientAuthServlet.class.getName());

    private ClientAuthService clientAuthService;

    @Override
    public void init() throws ServletException {
        super.init();
        clientAuthService = new ClientAuthService();
        LOGGER.info("ClientAuthServlet initialisée.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
                System.out.println("Client déconnecté, session invalidée.");
            }
            // Redirige vers la page de connexion client
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // Redirection vers la page de login client
        } else {
            // Affiche la page de connexion client si aucune action spécifique n'est demandée.
            System.out.println("Accès à la page de connexion client.");
            request.getRequestDispatcher("clientLogin.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // CORRECTION ICI: Récupérer le 'cin' au lieu de 'email'
        String cin = request.getParameter("cin");
        String password = request.getParameter("password");

        // Utilise le service d'authentification client pour vérifier les identifiants
        // Assurez-vous que votre ClientAuthService.authenticate prend CIN et password
        Client client = clientAuthService.authenticate(cin, password); 

        if (client != null) {
            HttpSession session = request.getSession();
            session.setAttribute("client", client);
            session.setAttribute("role", "Client"); // Définit le rôle comme "Client"
            LOGGER.info("Client " + client.getCin() + " authentifié avec succès. Rôle: Client.");

            // CORRECTION MAJEURE ICI: Rediriger vers le tableau de bord client, PAS le tableau de bord général
            response.sendRedirect(request.getContextPath() + "/clientDashboard"); // Redirection vers le nouveau servlet clientDashboard
        } else {
            LOGGER.info("Échec de l'authentification pour CIN: " + cin);
            request.setAttribute("error", "CIN ou mot de passe incorrect.");
            request.getRequestDispatcher("clientLogin.jsp").forward(request, response);
        }
    }
}
