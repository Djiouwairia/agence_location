package com.agence.location.servlet;

import com.agence.location.model.Client;
import com.agence.location.model.Location;
import com.agence.location.model.Voiture;
import com.agence.location.service.ClientService;
import com.agence.location.service.LocationService;
import com.agence.location.service.ReportService;
import com.agence.location.service.VoitureService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet pour gérer le tableau de bord de l'interface client.
 * Il récupère toutes les données nécessaires pour les différents onglets (Aperçu, Voitures Disponibles, Mes Locations, Mon Profil).
 */
@WebServlet("/clientDashboard")
public class ClientDashboardServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ClientDashboardServlet.class.getName());

    private ClientService clientService;
    private LocationService locationService;
    private VoitureService voitureService;
    private ReportService reportService;

    @Override
    public void init() throws ServletException {
        super.init();
        clientService = new ClientService();
        locationService = new LocationService();
        voitureService = new VoitureService();
        reportService = new ReportService();
        LOGGER.info("ClientDashboardServlet initialisé.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("client") == null || !"Client".equals(session.getAttribute("role"))) {
            LOGGER.warning("Accès non autorisé à ClientDashboardServlet. Redirection vers la page de connexion client.");
            response.sendRedirect(request.getContextPath() + "/clientLogin.jsp");
            return;
        }

        Client loggedInClient = (Client) session.getAttribute("client");
        String clientCin = loggedInClient.getCin();

        // Récupérer les messages de la session si présents (après une redirection POST->GET)
        if (session.getAttribute("message") != null) {
            request.setAttribute("message", session.getAttribute("message"));
            session.removeAttribute("message");
        }
        if (session.getAttribute("error") != null) {
            request.setAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }

        try {
            // 1. Charger les données pour l'onglet "Aperçu"
            long clientRentalsCount = locationService.getClientRentalsCount(clientCin);
            request.setAttribute("clientRentalsCount", clientRentalsCount);

            long availableCarsClientCount = reportService.getNumberOfAvailableCars();
            request.setAttribute("availableCarsClientCount", availableCarsClientCount);

            List<Location> recentClientRentals = locationService.getRecentLocationsByClient(clientCin, 5);
            request.setAttribute("recentClientRentals", recentClientRentals);

            // 2. Charger les données pour l'onglet "Voitures Disponibles" (avec filtres potentiels)
            String marque = request.getParameter("marque");
            String categorie = request.getParameter("categorie");
            String prixMaxStr = request.getParameter("prixMax");
            String kilometrageMaxStr = request.getParameter("kilometrageMax");
            String nbPlacesStr = request.getParameter("nbPlaces");
            String typeCarburant = request.getParameter("typeCarburant");

            Double prixMax = (prixMaxStr != null && !prixMaxStr.isEmpty()) ? Double.parseDouble(prixMaxStr) : null;
            Double kilometrageMax = (kilometrageMaxStr != null && !kilometrageMaxStr.isEmpty()) ? Double.parseDouble(kilometrageMaxStr) : null;
            Integer nbPlaces = (nbPlacesStr != null && !nbPlacesStr.isEmpty()) ? Integer.parseInt(nbPlacesStr) : null;

            List<Voiture> voituresDisponibles = voitureService.searchAvailableVoitures(
                marque, kilometrageMax, null, typeCarburant, categorie, nbPlaces, prixMax);
            request.setAttribute("voituresDisponibles", voituresDisponibles);


            // 3. Charger les données pour l'onglet "Mes Locations"
            List<Location> clientLocations = locationService.getLocationsByClient(clientCin);
            request.setAttribute("clientLocations", clientLocations);

            // Gérer l'affichage du formulaire de location dans l'onglet "Mes Locations"
            String actionParam = request.getParameter("action");
            String immatriculationParam = request.getParameter("immatriculation");

            if ("showRentalForm".equals(actionParam) && immatriculationParam != null) {
                Voiture selectedVoiture = voitureService.getVoitureByImmatriculation(immatriculationParam);
                if (selectedVoiture != null) {
                    request.setAttribute("selectedVoiture", selectedVoiture);
                    request.setAttribute("showRentalForm", true); // Indique à la JSP d'afficher le formulaire
                } else {
                    LOGGER.warning("Voiture non trouvée pour l'immatriculation: " + immatriculationParam + " lors de la demande de location.");
                    request.setAttribute("error", "La voiture sélectionnée n'a pas été trouvée ou n'est plus disponible.");
                }
                request.setAttribute("tab", "mesLocations"); // S'assurer que l'onglet "Mes Locations" est actif
            } else if ("showNewRentalForm".equals(actionParam)) {
                request.setAttribute("showNewRentalForm", true); // Indique à la JSP d'afficher le formulaire vide
                request.setAttribute("tab", "mesLocations"); // S'assurer que l'onglet "Mes Locations" est actif
            }


            // 4. Charger les données pour l'onglet "Mon Profil"
            Client clientProfile = clientService.getClientByCin(clientCin);
            request.setAttribute("client", clientProfile);

            LOGGER.info("Données pour le tableau de bord client chargées pour CIN: " + clientCin);

            // Rediriger vers la JSP principale du tableau de bord client
            request.getRequestDispatcher("/WEB-INF/views/clientDashboard.jsp").forward(request, response); // Chemin corrigé

        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erreur grave lors du chargement du tableau de bord client pour CIN " + clientCin + ": " + e.getMessage(), e);
            request.setAttribute("error", "Une erreur est survenue lors du chargement de votre tableau de bord. Veuillez réessayer plus tard.");
            request.getRequestDispatcher("/WEB-INF/views/clientLogin.jsp").forward(request, response); // Chemin corrigé
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur inattendue lors du chargement du tableau de bord client pour CIN " + clientCin + ": " + e.getMessage(), e);
            request.setAttribute("error", "Une erreur inattendue est survenue. Veuillez contacter l'administrateur.");
            request.getRequestDispatcher("/WEB-INF/views/clientLogin.jsp").forward(request, response); // Chemin corrigé
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
