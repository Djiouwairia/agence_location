package com.agence.location.servlet.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.agence.location.dto.MonthlyReportDTO;
import com.agence.location.service.ReportService;
import com.google.gson.Gson;

@WebServlet("/api/locations/stats/monthly")
public class MonthlyFinancialStatsApiServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(MonthlyFinancialStatsApiServlet.class.getName());
    private ReportService reportService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        reportService = new ReportService();
        gson = new Gson();
        LOGGER.info("MonthlyFinancialStatsApiServlet initialisé.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            int year = LocalDate.now().getYear(); // Année par défaut
            int month = LocalDate.now().getMonthValue(); // Mois par défaut

            String yearParam = request.getParameter("year");
            String monthParam = request.getParameter("month");

            if (yearParam != null) {
                try {
                    year = Integer.parseInt(yearParam);
                } catch (NumberFormatException e) {
                    LOGGER.warning("Paramètre 'year' invalide : " + yearParam + ". Utilisation de l'année actuelle.");
                }
            }
            if (monthParam != null) {
                try {
                    month = Integer.parseInt(monthParam);
                } catch (NumberFormatException e) {
                    LOGGER.warning("Paramètre 'month' invalide : " + monthParam + ". Utilisation du mois actuel.");
                }
            }

            // Cette méthode devrait déjà exister et être implémentée dans ReportService.java
            MonthlyReportDTO monthlyReport = reportService.getMonthlyFinancialReport(year, month);

            // Assurez-vous que le DTO a des getters publics pour que Gson puisse le sérialiser correctement
            out.print(gson.toJson(monthlyReport));
            out.flush();
            LOGGER.info("Réponse à /api/locations/stats/monthly pour year=" + year + ", month=" + month);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des statistiques financières mensuelles : " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", "Erreur lors du chargement du bilan financier mensuel.")));
            out.flush();
        }
    }
}