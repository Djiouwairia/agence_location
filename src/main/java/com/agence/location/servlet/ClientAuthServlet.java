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
            response.sendRedirect("login.jsp?client=true"); // Redirige vers la page de connexion client
        } else {
            // Affiche la page de connexion client si aucune action spécifique n'est demandée.
            System.out.println("Accès à la page de connexion client.");
            request.getRequestDispatcher("login.jsp?client=true").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cin = request.getParameter("cin");
        String password = request.getParameter("password");

        Client client = clientAuthService.authenticate(cin, password);

        if (client != null) {
            HttpSession session = request.getSession();
            session.setAttribute("client", client);
            session.setAttribute("role", "Client"); // Définit le rôle comme "Client"
            LOGGER.info("Client " + client.getCin() + " authentifié avec succès. Rôle: Client.");

            // CORRECTION ICI: Utiliser forward au lieu de sendRedirect pour accéder à une JSP sous WEB-INF
            request.getRequestDispatcher("/WEB-INF/views/clientDashboard.jsp").forward(request, response);
        } else {
            LOGGER.info("Échec de l'authentification pour CIN: " + cin);
            request.setAttribute("error", "CIN ou mot de passe incorrect.");
            request.getRequestDispatcher("login.jsp?client=true").forward(request, response);
        }
    }
}
