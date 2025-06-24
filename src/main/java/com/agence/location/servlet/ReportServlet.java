package com.agence.location.servlet;

import com.agence.location.service.ReportService;
import com.agence.location.model.Client;
import com.agence.location.model.Location;
import com.agence.location.util.PdfGenerator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream; // Ajoutez cet import pour l'OutputStream
import java.util.List;

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

        if (session == null || session.getAttribute("utilisateur") == null || !"ChefAgence".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        OutputStream os = null; // Déclarez l'OutputStream ici pour le rendre accessible dans le bloc finally

        try {
            // Définir les en-têtes avant d'obtenir l'OutputStream
            response.setContentType("application/pdf");
            // IMPORTANT : Flush l'OutputStream dans tous les cas après l'écriture
            // Le conteneur se chargera de la fermeture finale

            switch (action) {
                case "exportClientListPdf":
                    List<Client> clientsToExport = reportService.getAllClientsForExport();
                    response.setHeader("Content-Disposition", "attachment; filename=liste_clients.pdf");
                    os = response.getOutputStream(); // Obtenez l'OutputStream
                    PdfGenerator.generateClientListPdf(clientsToExport, os);
                    break;
                case "exportRentedCarsPdf": // Ceci est l'action pour la liste des locations
                    List<Location> locationsToExport = reportService.getRentedCarsWithTenantInfo(); // Utilisez un nom de variable plus clair
                    response.setHeader("Content-Disposition", "attachment; filename=liste_voitures_louees.pdf");
                    os = response.getOutputStream(); // Obtenez l'OutputStream
                    // Assurez-vous que cette méthode est bien nommée generateLocationsListPdf dans PdfGenerator
                    PdfGenerator.generateLocationsListPdf(locationsToExport, os);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action de rapport inconnue.");
                    return; // Retourne pour éviter de tenter de flusher un stream non utilisé
            }
            // Il est crucial de flusher le stream après avoir écrit le PDF
            // Ceci garantit que toutes les données sont envoyées au client.
            if (os != null) {
                os.flush();
            }

        } catch (IOException e) {
            System.err.println("Erreur IOException lors de la génération du PDF : " + e.getMessage());
            e.printStackTrace();
            // Assurez-vous que la réponse n'a pas déjà été commise
            if (!response.isCommitted()) {
                request.setAttribute("errorMessage", "Erreur d'entrée/sortie lors de la génération du PDF: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
            }
        } catch (RuntimeException e) {
            System.err.println("Erreur RuntimeException lors de la génération du PDF (iText interne): " + e.getMessage());
            e.printStackTrace();
            if (!response.isCommitted()) {
                request.setAttribute("errorMessage", "Erreur inattendue lors de la génération du PDF: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.err.println("Erreur générale inattendue lors de la génération du PDF: " + e.getMessage());
            e.printStackTrace();
            if (!response.isCommitted()) {
                request.setAttribute("errorMessage", "Erreur inattendue lors de la génération du PDF: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
            }
        }
        // Pas de bloc finally pour fermer l'OutputStream ici, car le conteneur s'en charge.
        // Fermer l'OutputStream ici peut causer des problèmes de "java.lang.IllegalStateException: getOutputStream() has already been called for this response"
        // ou des problèmes si des erreurs se sont produites plus tôt et que le stream a déjà été fermé par le conteneur.
    }
}