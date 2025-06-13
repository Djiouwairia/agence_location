package com.agence.location.servlet;

import com.agence.location.dao.ClientDAO;
import com.agence.location.dao.LocationDAO;
import com.agence.location.dao.VoitureDAO;
import com.agence.location.model.Client;
import com.agence.location.model.Location;
import com.agence.location.model.Utilisateur;
import com.agence.location.model.Voiture;
import com.agence.location.util.PdfGenerator; // Pour la génération de PDF

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Servlet pour gérer les opérations liées aux locations de voitures.
 * Cela inclut l'enregistrement de nouvelles locations, la recherche de voitures disponibles,
 * l'enregistrement du retour des voitures et la génération de factures.
 */
@WebServlet("/locations")
public class LocationServlet extends HttpServlet {

    private ClientDAO clientDAO;
    private VoitureDAO voitureDAO;
    private LocationDAO locationDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        clientDAO = new ClientDAO();
        voitureDAO = new VoitureDAO();
        locationDAO = new LocationDAO();
    }

    /**
     * Gère les requêtes GET.
     * Actions :
     * - "list" : Affiche la liste de toutes les locations.
     * - "new" : Affiche le formulaire pour une nouvelle location.
     * - "searchAvailableCars" : Recherche les voitures disponibles selon des critères.
     * - "return" : Affiche le formulaire pour enregistrer le retour d'une voiture.
     * @param request L'objet HttpServletRequest.
     * @param response L'objet HttpServletResponse.
     * @throws ServletException Si une erreur de servlet survient.
     * @throws IOException Si une erreur d'entrée/sortie survient.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Vérification de l'authentification et du rôle (seul le gestionnaire peut gérer les locations)
        if (session == null || session.getAttribute("utilisateur") == null || !"Gestionnaire".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp"); // Redirige vers la page de connexion si non autorisé
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // Action par défaut
        }

        switch (action) {
            case "new":
                // Prépare le formulaire de nouvelle location (peut pré-remplir si CIN ou immat sont passés)
                String clientCin = request.getParameter("clientCin");
                if (clientCin != null && !clientCin.isEmpty()) {
                    request.setAttribute("selectedClient", clientDAO.findById(clientCin));
                }
                String voitureImmat = request.getParameter("voitureImmat");
                if (voitureImmat != null && !voitureImmat.isEmpty()) {
                    request.setAttribute("selectedVoiture", voitureDAO.findById(voitureImmat));
                }
                request.getRequestDispatcher("/WEB-INF/views/locationForm.jsp").forward(request, response);
                break;
            case "searchAvailableCars":
                // Récupère les critères de recherche de voitures
                String marque = request.getParameter("marque");
                String categorie = request.getParameter("categorie");
                // Note : Pour l'année et le kilométrage, ils sont généralement recherchés par le gestionnaire.
                // Ici, on se concentre sur les critères de base pour la location.
                // Les autres critères (kilométrageMax, anneeMiseCirculationMin, typeCarburant)
                // pourraient être ajoutés si le formulaire le permet.
                List<Voiture> availableCars = voitureDAO.searchVoitures(marque, null, null, null, categorie, "Disponible");
                request.setAttribute("availableCars", availableCars);
                request.setAttribute("currentSearchClientCin", request.getParameter("clientCin")); // Conserver le CIN du client sélectionné
                request.getRequestDispatcher("/WEB-INF/views/locationForm.jsp").forward(request, response);
                break;
            case "return":
                // Affiche le formulaire de retour pour une location spécifique
                Long locationIdToReturn = Long.parseLong(request.getParameter("id"));
                Location locationToReturn = locationDAO.findById(locationIdToReturn);
                if (locationToReturn != null && "En cours".equals(locationToReturn.getStatut())) {
                    request.setAttribute("locationToReturn", locationToReturn);
                    request.getRequestDispatcher("/WEB-INF/views/returnCarForm.jsp").forward(request, response);
                } else {
                    request.setAttribute("error", "Location non trouvée ou déjà terminée.");
                    response.sendRedirect("locations?action=list");
                }
                break;
            case "list":
            default:
                // Affiche la liste de toutes les locations
                List<Location> locations = locationDAO.findAll();
                request.setAttribute("locations", locations);
                request.getRequestDispatcher("/WEB-INF/views/locationList.jsp").forward(request, response);
                break;
        }
    }

    /**
     * Gère les requêtes POST.
     * Actions :
     * - "add" : Enregistre une nouvelle location.
     * - "return" : Enregistre le retour d'une voiture.
     * @param request L'objet HttpServletRequest.
     * @param response L'objet HttpServletResponse.
     * @throws ServletException Si une erreur de servlet survient.
     * @throws IOException Si une erreur d'entrée/sortie survient.
     */
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
            // Logique pour ajouter une nouvelle location
            String clientCin = request.getParameter("clientCin");
            String voitureImmat = request.getParameter("voitureImmat");
            String nombreJoursStr = request.getParameter("nombreJours");
            String dateDebutStr = request.getParameter("dateDebut");

            try {
                Client client = clientDAO.findById(clientCin);
                Voiture voiture = voitureDAO.findById(voitureImmat);
                int nombreJours = Integer.parseInt(nombreJoursStr);
                LocalDate dateDebut = LocalDate.parse(dateDebutStr);
                LocalDate dateRetourPrevue = dateDebut.plusDays(nombreJours);

                if (client == null || voiture == null) {
                    request.setAttribute("error", "Client ou voiture non trouvée.");
                    request.setAttribute("selectedClient", client);
                    request.setAttribute("selectedVoiture", voiture);
                    request.getRequestDispatcher("/WEB-INF/views/locationForm.jsp").forward(request, response);
                    return;
                }

                if (!"Disponible".equals(voiture.getStatut())) {
                    request.setAttribute("error", "La voiture sélectionnée n'est pas disponible pour la location.");
                    request.setAttribute("selectedClient", client);
                    request.setAttribute("selectedVoiture", voiture);
                    request.getRequestDispatcher("/WEB-INF/views/locationForm.jsp").forward(request, response);
                    return;
                }

                double montantTotal = (double) nombreJours * voiture.getPrixLocationJ();
                double kilometrageDepart = voiture.getKilometrage(); // Kilométrage actuel de la voiture

                Location newLocation = new Location(client, voiture, currentGestionnaire,
                        dateDebut, nombreJours, dateRetourPrevue, montantTotal, kilometrageDepart);

                // Enregistrer la nouvelle location
                locationDAO.save(newLocation);

                // Mettre à jour le statut de la voiture
                voiture.setStatut("Louee");
                voitureDAO.save(voiture);

                // Générer la facture PDF
                // Redirige vers une nouvelle page qui déclenchera le téléchargement du PDF
                response.sendRedirect("locations?action=generateInvoice&locationId=" + newLocation.getId());


            } catch (NumberFormatException | DateTimeParseException e) {
                request.setAttribute("error", "Format de données invalide pour les jours ou la date.");
                request.getRequestDispatcher("/WEB-INF/views/locationForm.jsp").forward(request, response);
            } catch (RuntimeException e) {
                request.setAttribute("error", "Erreur lors de l'enregistrement de la location : " + e.getMessage());
                e.printStackTrace();
                request.getRequestDispatcher("/WEB-INF/views/locationForm.jsp").forward(request, response);
            }
        } else if ("return".equals(action)) {
            // Logique pour enregistrer le retour d'une voiture
            Long locationId = Long.parseLong(request.getParameter("locationId"));
            String kilometrageRetourStr = request.getParameter("kilometrageRetour");

            try {
                double kilometrageRetour = Double.parseDouble(kilometrageRetourStr);
                Location location = locationDAO.findById(locationId);

                if (location == null || !"En cours".equals(location.getStatut())) {
                    request.setAttribute("error", "Location non trouvée ou déjà terminée.");
                    request.getRequestDispatcher("/WEB-INF/views/returnCarForm.jsp").forward(request, response);
                    return;
                }

                Voiture voiture = location.getVoiture();
                if (kilometrageRetour < voiture.getKilometrage()) { // Check against current car kilometrage, not location's start km
                     request.setAttribute("error", "Le kilométrage de retour ne peut pas être inférieur au kilométrage actuel de la voiture (" + voiture.getKilometrage() + " km).");
                     request.setAttribute("locationToReturn", location);
                     request.getRequestDispatcher("/WEB-INF/views/returnCarForm.jsp").forward(request, response);
                     return;
                }


                // Mettre à jour la location
                location.setDateRetourReelle(LocalDate.now());
                location.setKilometrageRetour(kilometrageRetour);
                location.setStatut("Terminee");
                locationDAO.save(location);

                // Mettre à jour la voiture
                voiture.setKilometrage(kilometrageRetour);
                voiture.setStatut("Disponible");
                voitureDAO.save(voiture);

                request.setAttribute("message", "Retour de voiture enregistré avec succès !");
                response.sendRedirect("locations?action=list");

            } catch (NumberFormatException e) {
                request.setAttribute("error", "Format de kilométrage invalide.");
                request.getRequestDispatcher("/WEB-INF/views/returnCarForm.jsp").forward(request, response);
            } catch (RuntimeException e) {
                request.setAttribute("error", "Erreur lors de l'enregistrement du retour : " + e.getMessage());
                e.printStackTrace();
                request.getRequestDispatcher("/WEB-INF/views/returnCarForm.jsp").forward(request, response);
            }
        } else if ("generateInvoice".equals(action)) {
            // Cette partie du code peut être déplacée dans un doGet ou appelée en interne après l'enregistrement
            // Pour l'instant, je vais la laisser ici comme un exemple de comment l'appel serait fait.
            // Il est préférable de faire une redirection vers un doGet("generateInvoice") pour le téléchargement.
            Long locationId = Long.parseLong(request.getParameter("locationId"));
            Location location = locationDAO.findById(locationId);

            if (location != null) {
                try {
                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition", "attachment; filename=facture_location_" + location.getId() + ".pdf");
                    PdfGenerator.generateInvoice(location, response.getOutputStream());
                } catch (Exception e) {
                    request.setAttribute("error", "Erreur lors de la génération de la facture : " + e.getMessage());
                    response.sendRedirect("locations?action=list"); // Redirige vers la liste en cas d'erreur
                    e.printStackTrace();
                }
            } else {
                request.setAttribute("error", "Location non trouvée pour la génération de facture.");
                response.sendRedirect("locations?action=list");
            }
        }
    }
}
