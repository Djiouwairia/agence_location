package com.agence.location.servlet.api;

import com.agence.location.dto.ClientStatsDTO; // Si utilisé, sinon enlever
import com.agence.location.service.ReportService;
import com.google.gson.Gson; // Pour la conversion JSON
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import java.util.logging.Level;

@WebServlet("/api/reports/car-stats") // L'URL de cette API
public class CarStatsApiServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(CarStatsApiServlet.class.getName());
    private ReportService reportService;
    private Gson gson; // Outil pour convertir des objets Java en JSON

    @Override
    public void init() throws ServletException {
        super.init();
        reportService = new ReportService();
        gson = new Gson();
        LOGGER.info("CarStatsApiServlet initialisée.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            long totalCars = reportService.getTotalNumberOfCars();
            long availableCars = reportService.getNumberOfAvailableCars();
            long rentedCars = reportService.getNumberOfRentedCars();
            int pendingRequests = reportService.getPendingRequestsCount();

            // Créer un Map ou un objet DTO simple pour encapsuler toutes les statistiques
            // Si vous avez un DTO comme ClientStatsDTO qui peut contenir ces infos, utilisez-le.
            // Sinon, un Map est suffisant pour des données ad-hoc.
            // Pour l'exemple, utilisons un Map si ClientStatsDTO ne correspond pas exactement.
            // Ou créez un CarStatsDTO si vous préférez.

            // Exemple avec un Map (simple pour le dashboard)
            java.util.Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("totalCars", totalCars);
            stats.put("availableCars", availableCars);
            stats.put("rentedCars", rentedCars);
            stats.put("pendingRequests", pendingRequests);

            String jsonResponse = gson.toJson(stats);
            out.print(jsonResponse);
            LOGGER.info("Statistiques des voitures envoyées en JSON.");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des statistiques des voitures: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ErrorResponse("Erreur interne du serveur lors de la récupération des statistiques des voitures.")));
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }

    // Classe interne pour les messages d'erreur JSON
    private static class ErrorResponse {
        String message;
        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}