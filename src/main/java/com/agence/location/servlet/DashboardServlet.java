package com.agence.location.servlet;

import com.agence.location.service.ReportService;
import com.agence.location.service.LocationService; // Reste importé pour la cohérence
import com.agence.location.service.VoitureService; // MAINTENANT UTILISÉ POUR LE GESTIONNAIRE
import com.agence.location.service.ClientService; // Reste importé pour la cohérence

import com.agence.location.model.Utilisateur;
import com.agence.location.model.Location;
import com.agence.location.model.Voiture;
import com.agence.location.dto.MonthlyReportDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet pour afficher les tableaux de bord du chef d'agence et des gestionnaires.
 * Délègue l'obtention des données de rapport à ReportService et VoitureService (pour le gestionnaire).
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DashboardServlet.class.getName());

    private ReportService reportService;
    private LocationService locationService; // Maintenu pour la cohérence, si d'autres parties l'utilisent
    private VoitureService voitureService; // Initialisé et utilisé pour les voitures disponibles
    private ClientService clientService; // Maintenu pour la cohérence

    @Override
    public void init() throws ServletException {
        super.init();
        reportService = new ReportService();
        locationService = new LocationService(); // Si LocationService a des méthodes utiles pour le gestionnaire
        voitureService = new VoitureService();   // Initialisation de VoitureService
        clientService = new ClientService();     // Si ClientService a des méthodes utiles pour le gestionnaire
        LOGGER.info("DashboardServlet initialisée.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("utilisateur") == null) {
            LOGGER.warning("Accès non authentifié au DashboardServlet. Redirection vers login.jsp");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        String role = utilisateur.getRole();

        request.setAttribute("utilisateur", utilisateur);
        request.setAttribute("role", role);

        LOGGER.info("DashboardServlet - doGet for user role: " + role);

        try {
            // =========================================================
            // Données COMMUNE à TOUS les tableaux de bord (Chef d'Agence et Gestionnaire)
            // Ces données sont récupérées via ReportService
            // =========================================================
            long nombreTotalVoitures = reportService.getTotalNumberOfCars();
            long nombreVoituresDisponibles = reportService.getNumberOfAvailableCars();
            long nombreVoituresLouees = reportService.getNumberOfRentedCars();
            int pendingRequestsCount = reportService.getPendingRequestsCount(); // Demandes en attente

            request.setAttribute("nombreTotalVoitures", nombreTotalVoitures);
            request.setAttribute("nombreVoituresDisponibles", nombreVoituresDisponibles);
            request.setAttribute("nombreVoituresLouees", nombreVoituresLouees);
            request.setAttribute("pendingRequestsCount", pendingRequestsCount);

            // Cette liste est déjà présente dans votre code et sera utilisée par chefDashboard.jsp.
            // Elle est pertinente pour les gestionnaires aussi.
            List<Location> voituresLoueesAvecInfosLocataires = reportService.getRentedCarsWithTenantInfo();
            request.setAttribute("voituresLoueesAvecInfosLocataires", voituresLoueesAvecInfosLocataires);


            if ("ChefAgence".equals(role)) {
                LOGGER.info("Chargement du tableau de bord du Chef d'Agence pour " + utilisateur.getUsername());

                // Fonctionnalités spécifiques au chef d'agence via ReportService
                List<Voiture> voituresPlusRecherches = reportService.getMostSearchedCars(5); // Top 5
                request.setAttribute("voituresPlusRecherches", voituresPlusRecherches);

                LocalDate now = LocalDate.now();
                MonthlyReportDTO bilanMensuel = reportService.getMonthlyFinancialReport(now.getYear(), now.getMonthValue());
                request.setAttribute("bilanMensuel", bilanMensuel);
                request.setAttribute("moisBilan", now.getMonth().name());
                request.setAttribute("anneeBilan", now.getYear());

                LOGGER.info("ChefAgence Dashboard data loaded.");
                request.getRequestDispatcher("/WEB-INF/views/chefDashboard.jsp").forward(request, response);

            } else if ("Gestionnaire".equals(role)) {
                LOGGER.info("Chargement du tableau de bord du Gestionnaire pour " + utilisateur.getUsername());

                // Données SPÉCIFIQUES au Gestionnaire:

                // 1. Récupération des voitures disponibles pour le tableau "Voitures Disponibles"
                List<Voiture> availableCars = voitureService.getAvailableVoitures();
                request.setAttribute("availableCars", availableCars); // C'est cet attribut qui est utilisé dans la JSP

                // 2. Si vous avez des méthodes existantes pour ces listes (et seulement si elles existent !) :
                // List<Location> pendingRequestsList = locationService.getPendingRentalRequests(); // Si cette méthode existe
                // request.setAttribute("pendingRequests", pendingRequestsList);
                
                // List<Location> currentRentalsList = locationService.getCurrentRentals(); // Si cette méthode existe
                // request.setAttribute("currentRentals", currentRentalsList);

                LOGGER.info("Gestionnaire Dashboard data loaded. Redirection vers gestionnaireDashboard.jsp.");
                request.getRequestDispatcher("/WEB-INF/views/gestionnaireDashboard.jsp").forward(request, response);
            } else {
                session.invalidate();
                request.setAttribute("error", "Accès non autorisé ou rôle inconnu.");
                LOGGER.warning("Rôle inconnu pour l'utilisateur: " + utilisateur.getUsername() + ", rôle: " + role);
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            }
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erreur grave lors du chargement du tableau de bord: " + e.getMessage(), e);
            request.setAttribute("error", "Une erreur est survenue lors du chargement du tableau de bord. Veuillez contacter l'administrateur.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}
