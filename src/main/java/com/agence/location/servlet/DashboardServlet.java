package com.agence.location.servlet;

import com.agence.location.service.ReportService;
import com.agence.location.service.LocationService; // Ajouté si vous voulez passer des stats spécifiques à LocationService au lieu de ReportService
import com.agence.location.service.VoitureService; // Ajouté si vous voulez passer des stats spécifiques à VoitureService
import com.agence.location.service.ClientService; // Ajouté si vous voulez passer des stats spécifiques à ClientService

import com.agence.location.model.Utilisateur;
import com.agence.location.model.Location; // Pour obtenir les informations sur les locataires (via ReportService)
import com.agence.location.model.Voiture; // Pour les voitures les plus recherchées (via ReportService)
import com.agence.location.dto.MonthlyReportDTO; // Import du DTO pour le bilan mensuel
// import com.agence.location.dto.ClientStatsDTO; // N'est pas directement utilisé ici si ReportService gère tout

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
 * Délègue l'obtention des données de rapport à ReportService.
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DashboardServlet.class.getName());

    private ReportService reportService;
    // Si vous aviez des services spécifiques en plus de ReportService, vous pouvez les garder ici:
    // private LocationService locationService;
    // private VoitureService voitureService;
    // private ClientService clientService;


    @Override
    public void init() throws ServletException {
        super.init();
        reportService = new ReportService();
        // locationService = new LocationService(); // Décommenter si vous les utilisez
        // voitureService = new VoitureService();
        // clientService = new ClientService();
        LOGGER.info("DashboardServlet initialisée.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("utilisateur") == null) {
            LOGGER.warning("Accès non authentifié au DashboardServlet. Redirection vers login.jsp");
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // Utilise getContextPath()
            return;
        }

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        String role = utilisateur.getRole();

        request.setAttribute("utilisateur", utilisateur);
        request.setAttribute("role", role);

        LOGGER.info("DashboardServlet - doGet for user role: " + role);

        try {
            // Récupération des données du parking via ReportService pour les deux rôles (Gestionnaire et ChefAgence)
            long nombreTotalVoitures = reportService.getTotalNumberOfCars();
            long nombreVoituresDisponibles = reportService.getNumberOfAvailableCars();
            long nombreVoituresLouees = reportService.getNumberOfRentedCars();
            int pendingRequestsCount = reportService.getPendingRequestsCount(); // Demandes en attente

            request.setAttribute("nombreTotalVoitures", nombreTotalVoitures);
            request.setAttribute("nombreVoituresDisponibles", nombreVoituresDisponibles);
            request.setAttribute("nombreVoituresLouees", nombreVoituresLouees);
            request.setAttribute("pendingRequestsCount", pendingRequestsCount); // Passer cette valeur à la JSP

            // Données pour les tableaux supplémentaires : Voitures actuellement louées avec infos locataires
            List<Location> voituresLoueesAvecInfosLocataires = reportService.getRentedCarsWithTenantInfo();
            request.setAttribute("voituresLoueesAvecInfosLocataires", voituresLoueesAvecInfosLocataires);

            if ("ChefAgence".equals(role)) {
                // Fonctionnalités spécifiques au chef d'agence via ReportService
                List<Voiture> voituresPlusRecherches = reportService.getMostSearchedCars(5); // Top 5
                request.setAttribute("voituresPlusRecherches", voituresPlusRecherches);

                LocalDate now = LocalDate.now();
                MonthlyReportDTO bilanMensuel = reportService.getMonthlyFinancialReport(now.getYear(), now.getMonthValue());
                request.setAttribute("bilanMensuel", bilanMensuel);
                request.setAttribute("moisBilan", now.getMonth().name()); // Conserve .name() pour la simplicité
                request.setAttribute("anneeBilan", now.getYear());

                // Si vous aviez ClientStatsDTO et une méthode pour les meilleurs clients dans ReportService, utilisez-la ici:
                // List<ClientStatsDTO> topClients = reportService.getTopClientsByCompletedRentals();
                // request.setAttribute("topClients", topClients);


                LOGGER.info("ChefAgence Dashboard data loaded.");
                request.getRequestDispatcher("/WEB-INF/views/chefDashboard.jsp").forward(request, response);

            } else if ("Gestionnaire".equals(role)) {
                // Le gestionnaire utilise les données de parking, les demandes en attente et les locations en cours.
                LOGGER.info("Gestionnaire Dashboard data loaded.");
                request.getRequestDispatcher("/WEB-INF/views/gestionnaireDashboard.jsp").forward(request, response);
            } else {
                session.invalidate();
                request.setAttribute("error", "Accès non autorisé ou rôle inconnu.");
                LOGGER.warning("Rôle inconnu pour l'utilisateur: " + utilisateur.getUsername() + ", rôle: " + role);
                response.sendRedirect(request.getContextPath() + "/login.jsp"); // Utilise getContextPath()
            }
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erreur grave lors du chargement du tableau de bord: " + e.getMessage(), e);
            request.setAttribute("error", "Une erreur est survenue lors du chargement du tableau de bord. Veuillez contacter l'administrateur.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response); // Redirige vers la page de login par défaut
        }
    }
}
