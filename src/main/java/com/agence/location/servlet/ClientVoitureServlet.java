package com.agence.location.servlet;

import com.agence.location.model.Voiture;
import com.agence.location.service.VoitureService; // Pour récupérer les voitures
import com.agence.location.service.ReportService; // Ajouté pour getNumberOfAvailableCars dans ClientDashboard
import com.agence.location.service.LocationService; // Ajouté pour getClientRentalsCount et getRecentLocationsByClient
import com.agence.location.model.Client; // Pour vérifier l'authentification client
import com.agence.location.model.Location; // Pour List<Location>

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Servlet pour gérer les opérations liées aux voitures pour l'interface client.
 * Affiche la liste des voitures disponibles et les détails d'une voiture spécifique.
 */
@WebServlet("/clientVoitures")
public class ClientVoitureServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ClientVoitureServlet.class.getName());

    private VoitureService voitureService;
    private ReportService reportService; // Initialisation du ReportService
    private LocationService locationService; // Initialisation du LocationService

    @Override
    public void init() throws ServletException {
        super.init();
        voitureService = new VoitureService();
        reportService = new ReportService();
        locationService = new LocationService();
        LOGGER.info("ClientVoitureServlet initialisée.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Vérification de l'authentification du client
        if (session == null || session.getAttribute("client") == null || !"Client".equals(session.getAttribute("role"))) {
            LOGGER.warning("Accès non autorisé à ClientVoitureServlet. Redirection vers la page de connexion client.");
            response.sendRedirect(request.getContextPath() + "/login.jsp?client=true");
            return;
        }

        String action = request.getParameter("action");
        Client client = (Client) session.getAttribute("client");

        try {
            if (action == null || "listAvailable".equals(action)) {
                LOGGER.info("Action: listAvailable - Préparation des données pour le clientDashboard.jsp");
                
                // Récupération des voitures disponibles pour affichage dans clientDashboard ou une page dédiée
                List<Voiture> voituresDisponibles = voitureService.getAvailableVoitures();
                request.setAttribute("voituresDisponibles", voituresDisponibles); // Utile si clientDashboard affiche la liste

                // Statistiques pour les cartes du clientDashboard
                long availableCarsClientCount = reportService.getNumberOfAvailableCars(); // Récupère le nombre de voitures disponibles
                request.setAttribute("availableCarsClientCount", availableCarsClientCount);

                int clientRentalsCount = locationService.getClientRentalsCount(client.getCin()); // Méthode du LocationService
                request.setAttribute("clientRentalsCount", clientRentalsCount);

                List<Location> recentClientRentals = locationService.getRecentLocationsByClient(client.getCin(), 5); // 5 dernières locations
                request.setAttribute("recentClientRentals", recentClientRentals);

                request.getRequestDispatcher("/WEB-INF/views/clientDashboard.jsp").forward(request, response);

            } else if ("viewDetails".equals(action)) {
                String immatriculation = request.getParameter("immatriculation");
                if (immatriculation != null && !immatriculation.isEmpty()) {
                    LOGGER.info("Action: viewDetails - Récupération des détails de la voiture: " + immatriculation + " pour le client: " + client.getCin());
                    Voiture voiture = voitureService.getVoitureByImmatriculation(immatriculation);
                    if (voiture != null && "Disponible".equals(voiture.getStatut())) {
                        request.setAttribute("voiture", voiture);
                        request.getRequestDispatcher("/WEB-INF/views/clientCard.jsp").forward(request, response);
                    } else {
                        LOGGER.warning("Voiture non trouvée ou non disponible pour l'immatriculation: " + immatriculation);
                        request.setAttribute("error", "Voiture non trouvée ou non disponible.");
                        // Re-passer les données au dashboard pour l'affichage de l'erreur
                        List<Voiture> voituresDisponibles = voitureService.getAvailableVoitures();
                        request.setAttribute("voituresDisponibles", voituresDisponibles);
                        long availableCarsClientCount = reportService.getNumberOfAvailableCars();
                        request.setAttribute("availableCarsClientCount", availableCarsClientCount);
                        int clientRentalsCount = locationService.getClientRentalsCount(client.getCin());
                        request.setAttribute("clientRentalsCount", clientRentalsCount);
                        List<Location> recentClientRentals = locationService.getRecentLocationsByClient(client.getCin(), 5);
                        request.setAttribute("recentClientRentals", recentClientRentals);
                        request.getRequestDispatcher("/WEB-INF/views/clientDashboard.jsp").forward(request, response);
                    }
                } else {
                    LOGGER.warning("Paramètre 'immatriculation' manquant pour l'action viewDetails.");
                    request.setAttribute("error", "Immatriculation de la voiture manquante.");
                    // Re-passer les données au dashboard pour l'affichage de l'erreur
                    List<Voiture> voituresDisponibles = voitureService.getAvailableVoitures();
                    request.setAttribute("voituresDisponibles", voituresDisponibles);
                    long availableCarsClientCount = reportService.getNumberOfAvailableCars();
                    request.setAttribute("availableCarsClientCount", availableCarsClientCount);
                    int clientRentalsCount = locationService.getClientRentalsCount(client.getCin());
                    request.setAttribute("clientRentalsCount", clientRentalsCount);
                    List<Location> recentClientRentals = locationService.getRecentLocationsByClient(client.getCin(), 5);
                    request.setAttribute("recentClientRentals", recentClientRentals);
                    request.getRequestDispatcher("/WEB-INF/views/clientDashboard.jsp").forward(request, response);
                }
            } else {
                LOGGER.warning("Action non reconnue pour ClientVoitureServlet: " + action);
                // Re-passer les données au dashboard pour l'affichage initial
                List<Voiture> voituresDisponibles = voitureService.getAvailableVoitures();
                request.setAttribute("voituresDisponibles", voituresDisponibles);
                long availableCarsClientCount = reportService.getNumberOfAvailableCars();
                request.setAttribute("availableCarsClientCount", availableCarsClientCount);
                int clientRentalsCount = locationService.getClientRentalsCount(client.getCin());
                request.setAttribute("clientRentalsCount", clientRentalsCount);
                List<Location> recentClientRentals = locationService.getRecentLocationsByClient(client.getCin(), 5);
                request.setAttribute("recentClientRentals", recentClientRentals);
                request.getRequestDispatcher("/WEB-INF/views/clientDashboard.jsp").forward(request, response);
            }
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'exécution de ClientVoitureServlet pour action " + action + ": " + e.getMessage(), e);
            request.setAttribute("error", "Une erreur est survenue lors du traitement de votre demande.");
            // Assurez-vous de passer les données minimales si redirection vers dashboard
            List<Voiture> voituresDisponibles = voitureService.getAvailableVoitures();
            request.setAttribute("voituresDisponibles", voituresDisponibles);
            long availableCarsClientCount = reportService.getNumberOfAvailableCars();
            request.setAttribute("availableCarsClientCount", availableCarsClientCount);
            // Vérifier si client est non null avant d'appeler getClientRentalsCount
            if (client != null) {
                request.setAttribute("clientRentalsCount", locationService.getClientRentalsCount(client.getCin()));
                request.setAttribute("recentClientRentals", locationService.getRecentLocationsByClient(client.getCin(), 5));
            } else {
                request.setAttribute("clientRentalsCount", 0);
                request.setAttribute("recentClientRentals", java.util.Collections.emptyList());
            }
            request.getRequestDispatcher("/WEB-INF/views/clientDashboard.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response); // Pour l'instant, toutes les requêtes POST renvoient au GET
    }
}
