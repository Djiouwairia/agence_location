package com.agence.location.servlet.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.agence.location.dto.FinancialDataDTO;
import com.agence.location.service.ReportService;
import com.google.gson.Gson;

@WebServlet("/api/reports/financial-over-period")
public class FinancialOverPeriodApiServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(FinancialOverPeriodApiServlet.class.getName());
    private ReportService reportService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        reportService = new ReportService();
        gson = new Gson();
        LOGGER.info("FinancialOverPeriodApiServlet initialisé.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String period = request.getParameter("period"); // ex: "3months", "6months", "currentYear", "all"
            if (period == null || period.isEmpty()) {
                period = "3months"; // Période par défaut
            }

            // Vous DEVEZ implémenter cette méthode dans ReportService.java
            // Elle doit interroger votre base de données pour obtenir les données financières agrégées par unités de temps pertinentes pour le graphique.
            List<FinancialDataDTO> financialData = reportService.getFinancialDataOverPeriod(period);

            out.print(gson.toJson(financialData));
            out.flush();
            LOGGER.info("Réponse à /api/reports/financial-over-period pour period=" + period);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des données financières par période : " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", "Erreur lors du chargement du graphique financier.")));
            out.flush();
        }
    }
}


