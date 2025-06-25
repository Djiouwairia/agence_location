package com.agence.location.servlet;

import com.agence.location.service.ClientService;
import com.agence.location.service.LocationService;
import com.agence.location.service.VoitureService;
import com.agence.location.model.Client;
import com.agence.location.model.Location;
import com.agence.location.model.Utilisateur;
import com.agence.location.model.Voiture;
import com.agence.location.util.PdfGenerator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level; // Import pour Level
import java.util.logging.Logger;

@WebServlet("/locations")
public class LocationServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(LocationServlet.class.getName());

    private ClientService clientService;
    private LocationService locationService;
    private VoitureService voitureService;

    @Override
    public void init() throws ServletException {
        super.init();
        clientService = new ClientService();
        locationService = new LocationService();
        voitureService = new VoitureService();
        LOGGER.info("LocationServlet initialized.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Vérification de l'authentification et du rôle du personnel
        // Seuls les gestionnaires et Chefs d'Agence peuvent accéder à cette servlet
        if (session == null || session.getAttribute("utilisateur") == null || 
            (!"Gestionnaire".equals(session.getAttribute("role")) && !"ChefAgence".equals(session.getAttribute("role")))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        Utilisateur currentUtilisateur = (Utilisateur) session.getAttribute("utilisateur"); // Récupérer l'utilisateur connecté
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        LOGGER.info("doGet - Action: " + action);

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
                request.setAttribute("param_marque", marque);
                request.setAttribute("param_categorie", categorie);
                request.getRequestDispatcher("/WEB-INF/views/locationForm.jsp").forward(request, response);
                break;
            case "return":
                Long locationIdToReturn = null;
                try {
                    locationIdToReturn = Long.parseLong(request.getParameter("id"));
                } catch (NumberFormatException e) {
                    LOGGER.warning("ID de location invalide pour le retour: " + request.getParameter("id"));
                    session.setAttribute("error", "ID de location invalide.");
                    response.sendRedirect(request.getContextPath() + "/locations?action=list");
                    return;
                }
                Location locationToReturn = locationService.getLocationByIdWithDetails(locationIdToReturn);
                if (locationToReturn != null && "En cours".equals(locationToReturn.getStatut())) {
                    request.setAttribute("locationToReturn", locationToReturn);

                    Date utilDateDebut = null;
                    if (locationToReturn.getDateDebut() != null) {
                        utilDateDebut = Date.from(locationToReturn.getDateDebut().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    }

                    Date utilDateRetourPrevue = null;
                    if (locationToReturn.getDateRetourPrevue() != null) {
                        utilDateRetourPrevue = Date.from(locationToReturn.getDateRetourPrevue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    }

                    request.setAttribute("utilDateDebut", utilDateDebut);
                    request.setAttribute("utilDateRetourPrevue", utilDateRetourPrevue);

                    request.getRequestDispatcher("/WEB-INF/views/returnCarForm.jsp").forward(request, response);
                } else {
                    LOGGER.warning("Location " + locationIdToReturn + " non trouvée ou déjà terminée pour le retour.");
                    session.setAttribute("error", "Location non trouvée ou déjà terminée.");
                    response.sendRedirect(request.getContextPath() + "/locations?action=list");
                }
                break;
            case "generateInvoice":
                Long invoiceLocationId = null;
                try {
                    invoiceLocationId = Long.parseLong(request.getParameter("locationId"));
                } catch (NumberFormatException e) {
                    LOGGER.warning("ID de location invalide pour la facture: " + request.getParameter("locationId"));
                    session.setAttribute("error", "ID de location invalide pour la facture.");
                    response.sendRedirect(request.getContextPath() + "/locations?action=list");
                    return;
                }
                Location invoiceLocation = locationService.getLocationByIdWithDetails(invoiceLocationId);

                if (invoiceLocation != null) {
                    try {
                        response.setContentType("application/pdf");
                        response.setHeader("Content-Disposition", "attachment; filename=facture_location_" + invoiceLocation.getId() + ".pdf");
                        PdfGenerator.generateInvoice(invoiceLocation, response.getOutputStream());
                        LOGGER.info("Facture générée avec succès pour la location ID: " + invoiceLocationId);
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE, "Erreur IOException lors de la génération de la facture pour ID " + invoiceLocationId + ": " + e.getMessage(), e);
                        session.setAttribute("error", "Erreur lors de la génération de la facture (IO) : " + e.getMessage());
                        response.sendRedirect(request.getContextPath() + "/locations?action=list");
                    } catch (RuntimeException e) {
                        LOGGER.log(Level.SEVERE, "Erreur RuntimeException lors de la génération de la facture pour ID " + invoiceLocationId + ": " + e.getMessage(), e);
                        session.setAttribute("error", "Erreur inattendue lors de la génération de la facture : " + e.getMessage());
                        response.sendRedirect(request.getContextPath() + "/locations?action=list");
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Erreur générale lors de la génération de la facture pour ID " + invoiceLocationId + ": " + e.getMessage(), e);
                        session.setAttribute("error", "Erreur inattendue lors de la génération de la facture : " + e.getMessage());
                        response.sendRedirect(request.getContextPath() + "/locations?action=list");
                    }
                } else {
                    LOGGER.warning("Location " + invoiceLocationId + " non trouvée pour la génération de facture.");
                    session.setAttribute("error", "Location non trouvée pour la génération de facture.");
                    response.sendRedirect(request.getContextPath() + "/locations?action=list");
                }
                break;

            case "exportList":
                try {
                    List<Location> allLocations = locationService.getAllLocationsWithDetails();
                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition", "attachment; filename=liste_locations_" + LocalDate.now() + ".pdf");
                    PdfGenerator.generateLocationsListPdf(allLocations, response.getOutputStream());
                    LOGGER.info("Liste des locations exportée en PDF.");
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Erreur lors de l'exportation de la liste des locations: " + e.getMessage(), e);
                    session.setAttribute("error", "Erreur lors de l'exportation de la liste des locations : " + e.getMessage());
                    response.sendRedirect(request.getContextPath() + "/locations?action=list");
                } catch (RuntimeException e) {
                    LOGGER.log(Level.SEVERE, "Erreur Runtime lors de l'exportation de la liste des locations: " + e.getMessage(), e);
                    session.setAttribute("error", "Erreur inattendue lors de l'exportation de la liste des locations : " + e.getMessage());
                    response.sendRedirect(request.getContextPath() + "/locations?action=list");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Erreur générale lors de l'exportation de la liste des locations: " + e.getMessage(), e);
                    session.setAttribute("error", "Erreur inattendue lors de l'exportation de la liste des locations : " + e.getMessage());
                    response.sendRedirect(request.getContextPath() + "/locations?action=list");
                }
                return;

            // NOUVEAUX CAS POUR ACCEPTER OU DÉCLINER UNE DEMANDE
            case "acceptRequest":
                Long acceptLocationId = null;
                try {
                    acceptLocationId = Long.parseLong(request.getParameter("id"));
                    locationService.acceptLocationRequest(acceptLocationId, currentUtilisateur); // Passer l'utilisateur connecté
                    session.setAttribute("message", "Demande de location ID " + acceptLocationId + " acceptée avec succès !");
                    LOGGER.info("Demande de location ID " + acceptLocationId + " acceptée par " + currentUtilisateur.getUsername());
                } catch (NumberFormatException e) {
                    session.setAttribute("error", "ID de demande de location invalide.");
                    LOGGER.log(Level.WARNING, "ID invalide pour acceptRequest: " + request.getParameter("id"), e);
                } catch (RuntimeException e) {
                    session.setAttribute("error", "Erreur lors de l'acceptation de la demande : " + e.getMessage());
                    LOGGER.log(Level.SEVERE, "Erreur Runtime lors de l'acceptation de la demande ID " + acceptLocationId + ": " + e.getMessage(), e);
                }
                response.sendRedirect(request.getContextPath() + "/locations?action=list"); // Toujours rediriger vers la liste
                break;

            case "declineRequest":
                Long declineLocationId = null;
                try {
                    declineLocationId = Long.parseLong(request.getParameter("id"));
                    locationService.declineLocationRequest(declineLocationId);
                    session.setAttribute("message", "Demande de location ID " + declineLocationId + " déclinée avec succès !");
                    LOGGER.info("Demande de location ID " + declineLocationId + " déclinée par " + currentUtilisateur.getUsername());
                } catch (NumberFormatException e) {
                    session.setAttribute("error", "ID de demande de location invalide.");
                    LOGGER.log(Level.WARNING, "ID invalide pour declineRequest: " + request.getParameter("id"), e);
                } catch (RuntimeException e) {
                    session.setAttribute("error", "Erreur lors du refus de la demande : " + e.getMessage());
                    LOGGER.log(Level.SEVERE, "Erreur Runtime lors du refus de la demande ID " + declineLocationId + ": " + e.getMessage(), e);
                }
                response.sendRedirect(request.getContextPath() + "/locations?action=list"); // Toujours rediriger vers la liste
                break;

            case "list":
            default:
                List<Location> locations = locationService.getAllLocationsWithDetails();
                request.setAttribute("locations", locations);
                request.getRequestDispatcher("/WEB-INF/views/locationList.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utilisateur") == null || 
            (!"Gestionnaire".equals(session.getAttribute("role")) && !"ChefAgence".equals(session.getAttribute("role")))) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        Utilisateur currentGestionnaire = (Utilisateur) session.getAttribute("utilisateur");

        LOGGER.info("doPost - Action: " + action);

        if ("add".equals(action)) {
            String clientCin = request.getParameter("clientCin");
            String voitureImmat = request.getParameter("voitureImmat");
            String nombreJoursStr = request.getParameter("nombreJours");
            String dateDebutStr = request.getParameter("dateDebut");

            try {
                int nombreJours = Integer.parseInt(nombreJoursStr);
                LocalDate dateDebut = LocalDate.parse(dateDebutStr);

                Location newLocation = locationService.createLocation(clientCin, voitureImmat, currentGestionnaire, dateDebut, nombreJours);

                session.setAttribute("message", "Location enregistrée avec succès !");
                response.sendRedirect(request.getContextPath() + "/locations?action=list");
                return;

            } catch (NumberFormatException | DateTimeParseException e) {
                LOGGER.log(Level.SEVERE, "Erreur de format lors de l'ajout de location: " + e.getMessage(), e);
                request.setAttribute("error", "Format de données invalide pour les jours ou la date : " + e.getMessage());
                request.setAttribute("selectedClient", clientService.getClientByCin(clientCin));
                request.setAttribute("selectedVoiture", voitureService.getVoitureByImmatriculation(voitureImmat));
                request.getRequestDispatcher("/WEB-INF/views/locationForm.jsp").forward(request, response);
            } catch (RuntimeException e) {
                LOGGER.log(Level.SEVERE, "Erreur Runtime lors de l'enregistrement de la location: " + e.getMessage(), e);
                request.setAttribute("error", "Erreur lors de l'enregistrement de la location : " + e.getMessage());
                request.setAttribute("selectedClient", clientService.getClientByCin(clientCin));
                request.setAttribute("selectedVoiture", voitureService.getVoitureByImmatriculation(voitureImmat));
                request.getRequestDispatcher("/WEB-INF/views/locationForm.jsp").forward(request, response);
            }
        } else if ("return".equals(action)) {
            Long locationId = null;
            try {
                locationId = Long.parseLong(request.getParameter("locationId"));
            } catch (NumberFormatException e) {
                LOGGER.warning("ID de location invalide pour le retour (POST): " + request.getParameter("locationId"));
                session.setAttribute("error", "ID de location invalide.");
                response.sendRedirect(request.getContextPath() + "/locations?action=list");
                return;
            }

            String kilometrageRetourStr = request.getParameter("kilometrageRetour");

            try {
                double kilometrageRetour = Double.parseDouble(kilometrageRetourStr);
                locationService.recordCarReturn(locationId, kilometrageRetour);

                session.setAttribute("message", "Retour de voiture enregistré avec succès !");
                response.sendRedirect(request.getContextPath() + "/locations?action=list");
                return;

            } catch (NumberFormatException e) {
                LOGGER.log(Level.SEVERE, "Format de kilométrage invalide lors du retour: " + e.getMessage(), e);
                request.setAttribute("error", "Format de kilométrage invalide.");
                Location locationToRepopulate = locationService.getLocationByIdWithDetails(locationId);
                if (locationToRepopulate != null) {
                    request.setAttribute("locationToReturn", locationToRepopulate);
                    Date utilDateDebut = null;
                    if (locationToRepopulate.getDateDebut() != null) {
                        utilDateDebut = Date.from(locationToRepopulate.getDateDebut().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    }
                    Date utilDateRetourPrevue = null;
                    if (locationToRepopulate.getDateRetourPrevue() != null) {
                        utilDateRetourPrevue = Date.from(locationToRepopulate.getDateRetourPrevue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    }
                    request.setAttribute("utilDateDebut", utilDateDebut);
                    request.setAttribute("utilDateRetourPrevue", utilDateRetourPrevue);
                }
                request.getRequestDispatcher("/WEB-INF/views/returnCarForm.jsp").forward(request, response);
            } catch (RuntimeException e) {
                LOGGER.log(Level.SEVERE, "Erreur Runtime lors de l'enregistrement du retour: " + e.getMessage(), e);
                request.setAttribute("error", "Erreur lors de l'enregistrement du retour : " + e.getMessage());
                Location locationToRepopulate = locationService.getLocationByIdWithDetails(locationId);
                if (locationToRepopulate != null) {
                    request.setAttribute("locationToReturn", locationToRepopulate);
                    Date utilDateDebut = null;
                    if (locationToRepopulate.getDateDebut() != null) {
                        utilDateDebut = Date.from(locationToRepopulate.getDateDebut().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    }
                    Date utilDateRetourPrevue = null;
                    if (locationToRepopulate.getDateRetourPrevue() != null) {
                        utilDateRetourPrevue = Date.from(locationToRepopulate.getDateRetourPrevue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    }
                    request.setAttribute("utilDateDebut", utilDateDebut);
                    request.setAttribute("utilDateRetourPrevue", utilDateRetourPrevue);
                }
                request.getRequestDispatcher("/WEB-INF/views/returnCarForm.jsp").forward(request, response);
            }
        }
    }
}
