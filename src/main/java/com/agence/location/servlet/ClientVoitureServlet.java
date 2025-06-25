package com.agence.location.servlet;

import com.agence.location.model.Voiture;
import com.agence.location.service.VoitureService; // Pour récupérer les voitures
import com.agence.location.model.Client; // Pour vérifier l'authentification client

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Servlet pour gérer les opérations liées aux voitures pour l'interface client.
 * Affiche la liste des voitures disponibles et les détails d'une voiture spécifique.
 */
@WebServlet("/clientVoitures") // Nouveau mapping pour les requêtes client liées aux voitures
public class ClientVoitureServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ClientVoitureServlet.class.getName());

    private VoitureService voitureService;

    @Override
    public void init() throws ServletException {
        super.init();
        voitureService = new VoitureService();
        LOGGER.info("ClientVoitureServlet initialisée.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Vérification de l'authentification du client
        // Uniquement les clients connectés doivent accéder à ces pages.
        if (session == null || session.getAttribute("client") == null || !"Client".equals(session.getAttribute("role"))) {
            LOGGER.warning("Accès non autorisé à ClientVoitureServlet. Redirection vers la page de connexion client.");
            // Correction: Utiliser getContextPath() pour une redirection robuste
            response.sendRedirect(request.getContextPath() + "/login.jsp?client=true");
            return;
        }

        String action = request.getParameter("action");
        Client client = (Client) session.getAttribute("client"); // Récupère l'objet client de la session

        if (action == null || "listAvailable".equals(action)) {
            // Afficher la liste des voitures disponibles
            LOGGER.info("Action: listAvailable - Récupération des voitures disponibles pour le client: " + client.getCin());
            List<Voiture> voituresDisponibles = voitureService.getAvailableVoitures();
            request.setAttribute("voituresDisponibles", voituresDisponibles);
            // Correction: Utiliser forward pour accéder à une JSP sous WEB-INF
            request.getRequestDispatcher("/WEB-INF/views/clientDashboard.jsp").forward(request, response);
        } else if ("viewDetails".equals(action)) {
            // Afficher les détails d'une voiture spécifique
            String immatriculation = request.getParameter("immatriculation");
            if (immatriculation != null && !immatriculation.isEmpty()) {
                LOGGER.info("Action: viewDetails - Récupération des détails de la voiture: " + immatriculation + " pour le client: " + client.getCin());
                Voiture voiture = voitureService.getVoitureByImmatriculation(immatriculation);
                if (voiture != null && "Disponible".equals(voiture.getStatut())) { // S'assurer que la voiture est disponible
                    request.setAttribute("voiture", voiture);
                    // Correction: Utiliser forward pour accéder à une JSP sous WEB-INF
                    request.getRequestDispatcher("/WEB-INF/views/clientCard.jsp").forward(request, response);
                } else {
                    LOGGER.warning("Voiture non trouvée ou non disponible pour l'immatriculation: " + immatriculation);
                    request.setAttribute("error", "Voiture non trouvée ou non disponible.");
                    // Correction: Utiliser forward vers le dashboard avec l'erreur
                    request.getRequestDispatcher("/WEB-INF/views/clientDashboard.jsp").forward(request, response);
                }
            } else {
                LOGGER.warning("Paramètre 'immatriculation' manquant pour l'action viewDetails.");
                request.setAttribute("error", "Immatriculation de la voiture manquante.");
                // Correction: Utiliser forward vers le dashboard avec l'erreur
                request.getRequestDispatcher("/WEB-INF/views/clientDashboard.jsp").forward(request, response);
            }
        } else {
            LOGGER.warning("Action non reconnue pour ClientVoitureServlet: " + action);
            // Correction: Utiliser forward vers le dashboard par défaut
            request.getRequestDispatcher("/WEB-INF/views/clientDashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Pour l'instant, les opérations POST (soumission de formulaire de demande)
        // sont gérées par ClientRentalServlet. Cette servlet ne devrait pas recevoir de POST pour ces actions.
        // Redirection vers la liste des voitures disponibles si un POST inattendu arrive ici.
        LOGGER.warning("POST inattendu reçu dans ClientVoitureServlet. Redirection vers la liste des voitures disponibles.");
        // Correction: Utiliser getContextPath() pour une redirection robuste
        response.sendRedirect(request.getContextPath() + "/clientVoitures?action=listAvailable");
    }
}
