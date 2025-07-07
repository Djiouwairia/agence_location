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

import com.agence.location.dto.VoitureRentalCountDTO;
import com.agence.location.service.ReportService;
import com.google.gson.Gson;

@WebServlet("/api/reports/most-rented-cars")
public class MostRentedCarsApiServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(MostRentedCarsApiServlet.class.getName());
    private ReportService reportService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        reportService = new ReportService();
        gson = new Gson();
        LOGGER.info("MostRentedCarsApiServlet initialisé.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            int limit = 5; // Limite par défaut
            String limitParam = request.getParameter("limit");
            if (limitParam != null) {
                try {
                    limit = Integer.parseInt(limitParam);
                } catch (NumberFormatException e) {
                    LOGGER.warning("Paramètre 'limit' invalide : " + limitParam + ". Utilisation de la valeur par défaut (5).");
                }
            }

            String period = request.getParameter("period"); // ex: "all", "3months", "6months", "currentYear"
            if (period == null || period.isEmpty()) {
                period = "all"; // Période par défaut
            }

            // Vous DEVEZ implémenter cette méthode dans ReportService.java
            // Elle doit interroger votre base de données pour obtenir les voitures les plus louées en fonction de la période et de la limite.
            List<VoitureRentalCountDTO> mostRentedCars = reportService.getMostRentedCars(limit, period);

            out.print(gson.toJson(mostRentedCars));
            out.flush();
            LOGGER.info("Réponse à /api/reports/most-rented-cars pour limit=" + limit + ", period=" + period);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des voitures les plus louées : " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", "Erreur lors du chargement des voitures les plus louées.")));
            out.flush();
        }
    }
}
