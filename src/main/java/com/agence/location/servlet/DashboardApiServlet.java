package com.agence.location.servlet; // Assurez-vous que le package est correct

import com.agence.location.service.ReportService;
import com.agence.location.dto.MonthlyReportDTO;
import com.agence.location.dto.VoitureRentalCountDTO;
import com.agence.location.dto.FinancialDataDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson; // N'oubliez pas d'ajouter cette dépendance à votre pom.xml

@WebServlet("/api/reports/*") // Ce servlet gérera toutes les requêtes sous /api/reports/
public class DashboardApiServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DashboardApiServlet.class.getName());
    private ReportService reportService;
    private Gson gson; // Pour la conversion JSON

    @Override
    public void init() throws ServletException {
        super.init();
        reportService = new ReportService();
        gson = new Gson(); // Initialisation de Gson
        LOGGER.info("DashboardApiServlet initialisé et prêt à servir les APIs de rapports.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Vérification de l'authentification et du rôle (seuls les ChefAgence)
        if (session == null || session.getAttribute("utilisateur") == null || !"ChefAgence".equals(session.getAttribute("role"))) {
            LOGGER.warning("Accès non autorisé à DashboardApiServlet (non ChefAgence ou non connecté).");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Accès non autorisé. Veuillez vous connecter en tant que Chef Agence.");
            gson.toJson(errorResponse, out);
            out.close();
            return;
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo(); // Ex: /car-stats, /most-rented-cars, etc.

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Chemin d'API non spécifié.");
                gson.toJson(errorResponse, out);
                LOGGER.warning("Requête sur /api/reports/ sans chemin d'API spécifié.");
                return;
            }

            switch (pathInfo) {
                case "/car-stats":
                    long totalCars = reportService.getTotalNumberOfCars();
                    long availableCars = reportService.getNumberOfAvailableCars();
                    long rentedCars = reportService.getNumberOfRentedCars();
                    int pendingRequests = reportService.getPendingRequestsCount();

                    Map<String, Long> stats = new HashMap<>();
                    stats.put("totalCars", totalCars);
                    stats.put("availableCars", availableCars);
                    stats.put("rentedCars", rentedCars);
                    stats.put("pendingRequests", (long) pendingRequests);

                    gson.toJson(stats, out);
                    LOGGER.info("API /api/reports/car-stats appelée. Données envoyées.");
                    break;

                case "/most-rented-cars":
                    int limit = 5; // Valeur par défaut
                    if (request.getParameter("limit") != null) {
                        try {
                            limit = Integer.parseInt(request.getParameter("limit"));
                        } catch (NumberFormatException e) {
                            LOGGER.warning("Paramètre 'limit' invalide pour /most-rented-cars: " + request.getParameter("limit"));
                        }
                    }
                    String period = request.getParameter("period") != null ? request.getParameter("period") : "all";
                    List<VoitureRentalCountDTO> mostRentedCars = reportService.getMostRentedCars(limit, period);
                    gson.toJson(mostRentedCars, out);
                    LOGGER.info("API /api/reports/most-rented-cars appelée. Limit: " + limit + ", Period: " + period + ". Données envoyées.");
                    break;

                case "/financial-over-period":
                    String financialPeriod = request.getParameter("period") != null ? request.getParameter("period") : "3months";
                    List<FinancialDataDTO> financialData = reportService.getFinancialDataOverPeriod(financialPeriod);
                    gson.toJson(financialData, out);
                    LOGGER.info("API /api/reports/financial-over-period appelée. Period: " + financialPeriod + ". Données envoyées.");
                    break;

                case "/monthly-financial-stats":
                    int month = 0;
                    int year = 0;
                    try {
                        month = Integer.parseInt(request.getParameter("month"));
                        year = Integer.parseInt(request.getParameter("year"));
                    } catch (NumberFormatException e) {
                        LOGGER.warning("Paramètres 'month' ou 'year' invalides pour /monthly-financial-stats: " + e.getMessage());
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        Map<String, String> error = new HashMap<>();
                        error.put("error", "Paramètres de mois ou année invalides.");
                        gson.toJson(error, out);
                        return;
                    }
                    MonthlyReportDTO monthlyReport = reportService.getMonthlyFinancialReport(year, month);
                    gson.toJson(monthlyReport, out);
                    LOGGER.info("API /api/reports/monthly-financial-stats appelée pour " + month + "/" + year + ". Données envoyées.");
                    break;

                default:
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404 Not Found
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "API endpoint non trouvé: " + pathInfo);
                    gson.toJson(errorResponse, out);
                    LOGGER.warning("API endpoint non trouvé: " + pathInfo);
                    break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du traitement de la requête API " + pathInfo + ": " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur interne du serveur lors du traitement de la requête API.");
            errorResponse.put("details", e.getMessage());
            gson.toJson(errorResponse, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    // Si des requêtes POST sont nécessaires pour les APIs du tableau de bord, implémentez doPost ici.
    // Pour l'instant, toutes les requêtes pour les données de rapports sont GET.
}