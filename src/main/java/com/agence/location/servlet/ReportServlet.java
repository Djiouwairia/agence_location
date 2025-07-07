package com.agence.location.servlet;

import com.agence.location.service.ReportService;
import com.agence.location.service.LocationService; // Ajouté explicitement pour l'appel à getLocationByIdWithDetails
import com.agence.location.model.Client;
import com.agence.location.model.Location;
import com.agence.location.util.PdfGenerator; // Import de la nouvelle classe PdfGenerator

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level; // Import pour Level
import java.util.logging.Logger; // Import pour Logger

@WebServlet("/reports")
public class ReportServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ReportServlet.class.getName()); // Initialisation du logger

    private ReportService reportService;
    // Ajouté pour pouvoir appeler getLocationByIdWithDetails sans créer une nouvelle instance à chaque fois
    private LocationService locationService;    

    @Override
    public void init() throws ServletException {
        super.init();
        reportService = new ReportService();
        locationService = new LocationService(); // Initialisation du LocationService
        LOGGER.info("ReportServlet initialisée.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Vérification de l'authentification et du rôle (seuls les ChefAgence)
        if (session == null || session.getAttribute("utilisateur") == null || !"ChefAgence".equals(session.getAttribute("role"))) {
            LOGGER.warning("Accès non autorisé à ReportServlet (non ChefAgence ou non connecté). Redirection vers la page de connexion.");
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // Utilise getContextPath()
            return;
        }

        String action = request.getParameter("action");
        OutputStream os = null;

        try {
            switch (action) {
                case "exportClientListPdf":
                    List<Client> clientsToExport = reportService.getAllClientsForExport(); // Utilise la nouvelle méthode
                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition", "attachment; filename=liste_clients.pdf");
                    os = response.getOutputStream();
                    PdfGenerator.generateClientListPdf(clientsToExport, os); // Appel à PdfGenerator pour ClientList
                    LOGGER.info("Génération du PDF de la liste des clients.");
                    break;
                case "exportRentedCarsPdf":
                    List<Location> locationsToExport = reportService.getRentedCarsWithTenantInfo(); // Utilise la méthode existante
                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition", "attachment; filename=liste_voitures_louees.pdf");
                    os = response.getOutputStream();
                    PdfGenerator.generateLocationsListPdf(locationsToExport, os); // Appel à PdfGenerator pour LocationsList
                    LOGGER.info("Génération du PDF de la liste des voitures louées.");
                    break;
                case "generateInvoice": // Action pour générer une facture unique
                    String locationIdStr = request.getParameter("locationId");
                    if (locationIdStr != null && !locationIdStr.isEmpty()) {
                        Long locationId = Long.parseLong(locationIdStr);
                        // Utilisation de l'instance de locationService déjà initialisée dans init()
                        Location location = locationService.getLocationByIdWithDetails(locationId);    
                        if (location != null) {
                            response.setContentType("application/pdf");
                            response.setHeader("Content-Disposition", "attachment; filename=facture_location_" + location.getId() + ".pdf");
                            os = response.getOutputStream();
                            PdfGenerator.generateInvoice(location, os); // Appel à generateInvoice
                            LOGGER.info("Génération de la facture PDF pour la location ID: " + locationId);
                        } else {
                            LOGGER.warning("Location ID " + locationId + " non trouvée pour la génération de facture.");
                            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Location non trouvée pour la facture.");
                            return;
                        }
                    } else {
                        LOGGER.warning("Paramètre 'locationId' manquant pour la génération de facture.");
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de location manquant.");
                        return;
                    }
                    break;
                default:
                    LOGGER.warning("Action de rapport inconnue: " + action);
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action de rapport inconnue.");
                    return;
            }
            // Il est crucial de flusher le stream après avoir écrit le PDF
            if (os != null) {
                os.flush();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur IOException lors de la génération du PDF : " + e.getMessage(), e);
            if (!response.isCommitted()) {
                request.setAttribute("error", "Erreur d'entrée/sortie lors de la génération du PDF: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
            }
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "Erreur RuntimeException lors de la génération du PDF (iText interne ou base de données): " + e.getMessage(), e);
            if (!response.isCommitted()) {
                request.setAttribute("error", "Erreur inattendue lors de la génération du PDF: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
            }
        } catch (Exception e) { // Capture toutes les autres exceptions (ex: DocumentException de iText)
            LOGGER.log(Level.SEVERE, "Erreur générale inattendue lors de la génération du PDF: " + e.getMessage(), e);
            if (!response.isCommitted()) {
                request.setAttribute("error", "Erreur inattendue lors de la génération du PDF: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
            }
        }
    }
}