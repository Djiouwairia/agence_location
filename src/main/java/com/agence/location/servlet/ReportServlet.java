package com.agence.location.servlet;

import com.agence.location.service.ReportService;
import com.agence.location.model.Client;
import com.agence.location.model.Location;
import com.agence.location.util.PdfGenerator;
// L'importation de com.itextpdf.text.DocumentException a été supprimée
// car elle est spécifique à iText 5.x et n'est plus utilisée avec iText 8.x.

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException; // IOException est toujours nécessaire pour les opérations d'E/S
import java.util.List;

/**
 * Servlet pour la génération de rapports et d'exports PDF.
 * Délègue la récupération des données à ReportService.
 */
@WebServlet("/reports")
public class ReportServlet extends HttpServlet {

    private ReportService reportService;

    @Override
    public void init() throws ServletException {
        super.init();
        reportService = new ReportService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Vérification de l'authentification et du rôle (seul ChefAgence a accès aux rapports d'exportation ici)
        if (session == null || session.getAttribute("utilisateur") == null || !"ChefAgence".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp"); // Redirige vers la page de connexion si non autorisé
            return;
        }

        String action = request.getParameter("action");

        try {
            switch (action) {
                case "exportClientListPdf":
                    List<Client> clientsToExport = reportService.getAllClientsForExport();
                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition", "attachment; filename=liste_clients.pdf");
                    PdfGenerator.generateClientListPdf(clientsToExport, response.getOutputStream());
                    break;
                case "exportRentedCarsPdf":
                    List<Location> rentedCarsLocations = reportService.getRentedCarsWithTenantInfo();
                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition", "attachment; filename=liste_voitures_louees.pdf");
                    PdfGenerator.generateRentedCarsListPdf(rentedCarsLocations, response.getOutputStream());
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action de rapport inconnue.");
                    break;
            }
        } catch (IOException e) { // Seule IOException est lancée par PdfGenerator (iText 8.x)
            System.err.println("Erreur IOException lors de la génération du PDF : " + e.getMessage());
            e.printStackTrace();
            // Redirige vers une page d'erreur générique ou affiche un message
            request.setAttribute("errorMessage", "Erreur interne lors de la génération du PDF (IO) : " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        } catch (RuntimeException e) { // Capture les RuntimeException si PdfGenerator en lance (ex: problème de police)
            System.err.println("Erreur RuntimeException lors de la génération du PDF : " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Erreur inattendue lors de la génération du PDF : " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        } catch (Exception e) { // Capture toute autre exception inattendue
            System.err.println("Erreur inattendue lors de la génération du PDF : " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("errorMessage", "Erreur inattendue lors de la génération du PDF : " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
