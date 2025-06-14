package com.agence.location.servlet;

import com.agence.location.service.ClientService;
import com.agence.location.service.LocationService;
import com.agence.location.service.VoitureService;
import com.agence.location.model.Client;
import com.agence.location.model.Location;
import com.agence.location.model.Utilisateur;
import com.agence.location.model.Voiture;
import com.agence.location.util.PdfGenerator;
// Suppression de l'import de com.itextpdf.text.DocumentException car non utilisée directement ici pour la capture

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException; // Reste nécessaire pour IOException
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Servlet pour gérer les opérations liées aux locations de voitures.
 * Délègue la logique métier à LocationService, ClientService et VoitureService.
 */
@WebServlet("/locations")
public class LocationServlet extends HttpServlet {

    private ClientService clientService;
    private LocationService locationService;
    private VoitureService voitureService;

    @Override
    public void init() throws ServletException {
        super.init();
        clientService = new ClientService();
        locationService = new LocationService();
        voitureService = new VoitureService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("utilisateur") == null || !"Gestionnaire".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "new":
                String clientCin = request.getParameter("clientCin");
                if (clientCin != null && !clientCin.isEmpty()) {
                    request.setAttribute("selectedClient", clientService.getClientByCin(clientCin));
                }
                String voitureImmat = request.getParameter("voitureImmat");
                if (voitureImmat != null && !voitureImmat.isEmpty()) {
                    request.setAttribute("selectedVoiture", voitureService.getVoitureByImmatriculation(voitureImmat));
                }
                request.getRequestDispatcher("/WEB-INF/views/locationForm.jsp").forward(request, response);
                break;
            case "searchAvailableCars":
                String marque = request.getParameter("marque");
                String categorie = request.getParameter("categorie");
                List<Voiture> availableCars = locationService.searchAvailableCars(marque, categorie);
                request.setAttribute("availableCars", availableCars);
                request.setAttribute("currentSearchClientCin", request.getParameter("clientCin"));
                request.getRequestDispatcher("/WEB-INF/views/locationForm.jsp").forward(request, response);
                break;
            case "return":
                Long locationIdToReturn = null;
                try {
                    locationIdToReturn = Long.parseLong(request.getParameter("id"));
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "ID de location invalide.");
                    response.sendRedirect("locations?action=list");
                    return;
                }
                Location locationToReturn = locationService.getLocationById(locationIdToReturn);
                if (locationToReturn != null && "En cours".equals(locationToReturn.getStatut())) {
                    request.setAttribute("locationToReturn", locationToReturn);
                    request.getRequestDispatcher("/WEB-INF/views/returnCarForm.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Location non trouvée ou déjà terminée.");
                    response.sendRedirect("locations?action=list");
                }
                break;
            case "generateInvoice":
                Long invoiceLocationId = null;
                try {
                    invoiceLocationId = Long.parseLong(request.getParameter("locationId"));
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "ID de location invalide pour la facture.");
                    response.sendRedirect("locations?action=list");
                    return;
                }
                Location invoiceLocation = locationService.getLocationById(invoiceLocationId);

                if (invoiceLocation != null) {
                    try {
                        response.setContentType("application/pdf");
                        response.setHeader("Content-Disposition", "attachment; filename=facture_location_" + invoiceLocation.getId() + ".pdf");
                        // L'appel à PdfGenerator.generateInvoice() ne lance que IOException avec iText 8.x
                        PdfGenerator.generateInvoice(invoiceLocation, response.getOutputStream());
                    } catch (IOException e) { // Exception d'entrée/sortie
                        System.err.println("Erreur IOException lors de la génération de la facture : " + e.getMessage());
                        request.setAttribute("error", "Erreur lors de la génération de la facture (IO) : " + e.getMessage());
                        request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                        e.printStackTrace();
                    } catch (RuntimeException e) { // Capture les RuntimeException si PdfGenerator en lance pour problèmes de police par exemple
                        System.err.println("Erreur RuntimeException lors de la génération de la facture : " + e.getMessage());
                        request.setAttribute("error", "Erreur inattendue lors de la génération de la facture : " + e.getMessage());
                        request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                        e.printStackTrace();
                    } catch (Exception e) { // Capture toute autre exception inattendue
                        System.err.println("Erreur inattendue lors de la génération de la facture : " + e.getMessage());
                        request.setAttribute("error", "Erreur inattendue lors de la génération de la facture : " + e.getMessage());
                        request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                        e.printStackTrace();
                    }
                } else {
                    request.setAttribute("error", "Location non trouvée pour la génération de facture.");
                    request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                }
                break;
            case "list":
            default:
                List<Location> locations = locationService.getAllLocations();
                request.setAttribute("locations", locations);
                request.getRequestDispatcher("/WEB-INF/views/locationList.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utilisateur") == null || !"Gestionnaire".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        Utilisateur currentGestionnaire = (Utilisateur) session.getAttribute("utilisateur");

        if ("add".equals(action)) {
            String clientCin = request.getParameter("clientCin");
            String voitureImmat = request.getParameter("voitureImmat");
            String nombreJoursStr = request.getParameter("nombreJours");
            String dateDebutStr = request.getParameter("dateDebut");

            try {
                int nombreJours = Integer.parseInt(nombreJoursStr);
                LocalDate dateDebut = LocalDate.parse(dateDebutStr);

                Location newLocation = locationService.createLocation(clientCin, voitureImmat, currentGestionnaire, dateDebut, nombreJours);

                // Redirige vers le doGet pour générer la facture et la télécharger
                response.sendRedirect("locations?action=generateInvoice&locationId=" + newLocation.getId());

            } catch (NumberFormatException | DateTimeParseException e) {
                request.setAttribute("error", "Format de données invalide pour les jours ou la date : " + e.getMessage());
                request.setAttribute("selectedClient", clientService.getClientByCin(clientCin));
                request.setAttribute("selectedVoiture", voitureService.getVoitureByImmatriculation(voitureImmat));
                request.getRequestDispatcher("/WEB-INF/views/locationForm.jsp").forward(request, response);
            } catch (RuntimeException e) {
                request.setAttribute("error", "Erreur lors de l'enregistrement de la location : " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("selectedClient", clientService.getClientByCin(clientCin));
                request.setAttribute("selectedVoiture", voitureService.getVoitureByImmatriculation(voitureImmat));
                request.getRequestDispatcher("/WEB-INF/views/locationForm.jsp").forward(request, response);
            }
        } else if ("return".equals(action)) {
            Long locationId = Long.parseLong(request.getParameter("locationId"));
            String kilometrageRetourStr = request.getParameter("kilometrageRetour");

            try {
                double kilometrageRetour = Double.parseDouble(kilometrageRetourStr);
                locationService.recordCarReturn(locationId, kilometrageRetour);

                request.setAttribute("message", "Retour de voiture enregistré avec succès !");
                response.sendRedirect("locations?action=list");

            } catch (NumberFormatException e) {
                request.setAttribute("error", "Format de kilométrage invalide.");
                request.setAttribute("locationToReturn", locationService.getLocationById(locationId));
                request.getRequestDispatcher("/WEB-INF/views/returnCarForm.jsp").forward(request, response);
            } catch (RuntimeException e) {
                request.setAttribute("error", "Erreur lors de l'enregistrement du retour : " + e.getMessage());
                e.printStackTrace();
                request.setAttribute("locationToReturn", locationService.getLocationById(locationId));
                request.getRequestDispatcher("/WEB-INF/views/returnCarForm.jsp").forward(request, response);
            }
        }
    }
}
