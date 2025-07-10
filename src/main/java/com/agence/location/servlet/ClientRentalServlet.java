package com.agence.location.servlet;

import com.agence.location.model.Client;
import com.agence.location.model.Location;
import com.agence.location.model.Voiture;
import com.agence.location.service.LocationService;
import com.agence.location.service.VoitureService;
import com.agence.location.dto.RentalFormData; // Importez le nouveau DTO

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List; // Importation de java.util.List
import java.text.DecimalFormat; // Pour formater le double proprement
import java.text.DecimalFormatSymbols; // Pour s'assurer du séparateur décimal (point)
import java.util.Locale; // Pour définir la locale

/**
 * Servlet pour gérer les demandes de location des clients.
 * - Traite la soumission de nouvelles demandes de location.
 * - Affiche les locations en cours et passées du client.
 * - Permet d'annuler une demande de location en attente.
 */
@WebServlet("/clientRental")
public class ClientRentalServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ClientRentalServlet.class.getName());

    private LocationService locationService;
    private VoitureService voitureService;

    @Override
    public void init() throws ServletException {
        super.init();
        locationService = new LocationService();
        voitureService = new VoitureService(); // Initialise VoitureService
        LOGGER.info("ClientRentalServlet initialisée.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // 1. Vérification de l'authentification du client
        if (session == null || session.getAttribute("client") == null || !"Client".equals(session.getAttribute("role"))) {
            LOGGER.warning("Accès non autorisé à ClientRentalServlet (GET). Redirection vers la page de connexion client.");
            response.sendRedirect(request.getContextPath() + "/clientLogin.jsp?client=true");
            return;
        }

        String action = request.getParameter("action");
        Client loggedInClient = (Client) session.getAttribute("client");
        // String clientCin = loggedInClient.getCin(); // Pas directement utilisé ici, mais utile pour d'autres actions GET

        // Récupérer les messages de la session si présents (après une redirection POST->GET)
        if (session.getAttribute("message") != null) {
            request.setAttribute("message", session.getAttribute("message"));
            session.removeAttribute("message");
        }
        if (session.getAttribute("error") != null) {
            request.setAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }

        try {
            if ("showRentalForm".equals(action)) {
                String immatriculationVoiture = request.getParameter("immatriculation");
                Voiture selectedVoiture = voitureService.getVoitureByImmatriculation(immatriculationVoiture);

                if (selectedVoiture != null) {
                    request.setAttribute("selectedVoiture", selectedVoiture);
                    // Initialiser formData pour le formulaire (même si vide, pour éviter NullPointer dans JSP)
                    // Si vous avez des valeurs à pré-remplir (ex: depuis une erreur de soumission), utilisez-le ici.
                    RentalFormData formData = (RentalFormData) request.getAttribute("formData"); // CORRECTION ICI: getAttribute
                    if (formData == null) {
                        formData = new RentalFormData("", "", "0.00"); // Valeurs par défaut
                    }
                    request.setAttribute("formData", formData);

                    request.getRequestDispatcher("/WEB-INF/views/clientRentalRequest.jsp").forward(request, response);
                } else {
                    LOGGER.warning("Voiture non trouvée pour l'immatriculation: " + immatriculationVoiture);
                    session.setAttribute("error", "La voiture sélectionnée n'a pas été trouvée ou n'est plus disponible.");
                    response.sendRedirect(request.getContextPath() + "/clientDashboard?tab=cars"); // Rediriger vers la liste des voitures
                }
            } else if ("listMyRentals".equals(action)) {
                // Cette action est normalement gérée par ClientDashboardServlet qui inclut clientLocations.jsp
                // Mais si elle est appelée directement, assurez-vous que clientCin est disponible.
                List<Location> clientLocations = locationService.getLocationsByClient(loggedInClient.getCin());
                request.setAttribute("clientLocationList", clientLocations);
                request.getRequestDispatcher("/WEB-INF/views/clientLocationList.jsp").forward(request, response);
            } else if ("cancelRequest".equals(action)) { // Ajout de la gestion de l'annulation
                Long locationId = null;
                try {
                    locationId = Long.parseLong(request.getParameter("id"));
                    locationService.cancelRentalRequest(locationId);
                    session.setAttribute("message", "La demande de location a été annulée avec succès.");
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "ID de location invalide pour annulation: " + request.getParameter("id"), e);
                    session.setAttribute("error", "ID de location invalide.");
                } catch (RuntimeException e) {
                    LOGGER.log(Level.SEVERE, "Erreur lors de l'annulation de la demande de location ID " + locationId + ": " + e.getMessage(), e);
                    session.setAttribute("error", "Erreur lors de l'annulation de la demande : " + e.getMessage());
                }
                response.sendRedirect(request.getContextPath() + "/clientDashboard?tab=rentals");
            }
            // Ajoutez d'autres actions GET ici si nécessaire
            else {
                LOGGER.warning("Action non reconnue pour ClientRentalServlet (GET): " + action + ". Redirection par défaut vers le dashboard client.");
                // Redirection par défaut si l'action n'est pas reconnue
                response.sendRedirect(request.getContextPath() + "/clientDashboard?tab=overview");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur dans ClientRentalServlet doGet pour action " + action + ": " + e.getMessage(), e);
            session.setAttribute("error", "Une erreur est survenue lors du traitement de votre demande.");
            response.sendRedirect(request.getContextPath() + "/clientDashboard?tab=overview");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("client") == null || !"Client".equals(session.getAttribute("role"))) {
            LOGGER.warning("Accès non autorisé à ClientRentalServlet (POST). Redirection vers la page de connexion client.");
            response.sendRedirect(request.getContextPath() + "/clientLogin.jsp?client=true");
            return;
        }

        String action = request.getParameter("action");
        Client loggedInClient = (Client) session.getAttribute("client");
        String clientCin = loggedInClient.getCin();

        // Variables pour repopuler le formulaire en cas d'erreur
        String immatriculationVoiture = request.getParameter("immatriculationVoiture");
        String dateDebutStr = request.getParameter("dateDebut");
        String nombreJoursStr = request.getParameter("nombreJours");
        String montantTotalEstimeStr = request.getParameter("montantTotalEstime");
        // Nettoyer le montant total estimé pour s'assurer qu'il est parsable en double (remplacer virgule par point)
        String cleanMontantTotalEstime = (montantTotalEstimeStr != null && !montantTotalEstimeStr.isEmpty()) ? montantTotalEstimeStr.replace(",", ".") : "0.00";


        if ("request".equals(action)) {
            try {
                // Validation des entrées
                if (immatriculationVoiture == null || immatriculationVoiture.isEmpty() ||
                    dateDebutStr == null || dateDebutStr.isEmpty() ||
                    nombreJoursStr == null || nombreJoursStr.isEmpty() ||
                    montantTotalEstimeStr == null || montantTotalEstimeStr.isEmpty()) {
                    throw new IllegalArgumentException("Tous les champs obligatoires doivent être remplis.");
                }

                LocalDate dateDebut = LocalDate.parse(dateDebutStr);
                int nombreJours = Integer.parseInt(nombreJoursStr);
                double montantTotalEstime = Double.parseDouble(cleanMontantTotalEstime);

                // Vérifier que la date de début n'est pas passée
                if (dateDebut.isBefore(LocalDate.now())) {
                    throw new IllegalArgumentException("La date de début de location ne peut pas être antérieure à aujourd'hui.");
                }

                // Récupérer la voiture et le client
                Voiture voiture = voitureService.getVoitureByImmatriculation(immatriculationVoiture);
                Client client = (Client) session.getAttribute("client"); // Le client est déjà en session

                if (voiture == null) {
                    throw new RuntimeException("Voiture non trouvée.");
                }
                if (!"Disponible".equals(voiture.getStatut())) {
                    throw new RuntimeException("La voiture n'est pas disponible pour la location.");
                }

                // Créer l'objet Location
                Location nouvelleLocation = new Location();
                nouvelleLocation.setClient(client);
                nouvelleLocation.setVoiture(voiture);
                nouvelleLocation.setDateDebut(dateDebut);
                nouvelleLocation.setNombreJours(nombreJours);
                nouvelleLocation.setMontantTotal(montantTotalEstime);
                nouvelleLocation.setStatut("En attente"); // Statut initial

                // ** MODIFICATION AJOUTÉE ICI **
                LocalDate dateRetourPrevue = dateDebut.plusDays(nombreJours);
                nouvelleLocation.setDateRetourPrevue(dateRetourPrevue); 
                // ** FIN DE LA MODIFICATION **

                locationService.addRentalRequest(nouvelleLocation); // Appel de la méthode addRentalRequest

                session.setAttribute("message", "Votre demande de location a été soumise avec succès et est en attente de validation !");
                response.sendRedirect(request.getContextPath() + "/clientDashboard?tab=rentals"); // Rediriger vers l'onglet Mes Locations
            } catch (DateTimeParseException e) {
                LOGGER.log(Level.WARNING, "Format de date invalide: " + dateDebutStr, e);
                request.setAttribute("error", "Le format de la date de début est invalide. Utilisez AAAA-MM-JJ.");
                repopulateClientCardForm(request, immatriculationVoiture, dateDebutStr, nombreJoursStr, cleanMontantTotalEstime);
                request.getRequestDispatcher("/WEB-INF/views/clientRentalRequest.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Format numérique invalide pour nombreJours ou montantTotalEstime: " + e.getMessage(), e);
                request.setAttribute("error", "Le nombre de jours ou le montant estimé est invalide.");
                repopulateClientCardForm(request, immatriculationVoiture, dateDebutStr, nombreJoursStr, cleanMontantTotalEstime);
                request.getRequestDispatcher("/WEB-INF/views/clientRentalRequest.jsp").forward(request, response);
            } catch (IllegalArgumentException e) {
                LOGGER.log(Level.WARNING, "Validation échouée pour la demande de location: " + e.getMessage(), e);
                request.setAttribute("error", e.getMessage());
                repopulateClientCardForm(request, immatriculationVoiture, dateDebutStr, nombreJoursStr, cleanMontantTotalEstime);
                request.getRequestDispatcher("/WEB-INF/views/clientRentalRequest.jsp").forward(request, response);
            } catch (RuntimeException e) {
                request.setAttribute("error", "Une erreur est survenue lors de la soumission de votre demande : " + e.getMessage());
                LOGGER.log(Level.SEVERE, "Erreur Runtime dans doPost pour l'action 'request': " + e.getMessage(), e);
                repopulateClientCardForm(request, immatriculationVoiture, dateDebutStr, nombreJoursStr, cleanMontantTotalEstime);
                request.getRequestDispatcher("/WEB-INF/views/clientRentalRequest.jsp").forward(request, response);
            } catch (Exception e) {
                request.setAttribute("error", "Une erreur inattendue est survenue : " + e.getClass().getName() + " - " + e.getMessage());
                LOGGER.log(Level.SEVERE, "Erreur inattendue dans doPost pour l'action 'request': " + e.getMessage(), e);
                repopulateClientCardForm(request, immatriculationVoiture, dateDebutStr, nombreJoursStr, cleanMontantTotalEstime); // Passer la valeur propre
                request.getRequestDispatcher("/WEB-INF/views/clientRentalRequest.jsp").forward(request, response);
            }
        } else {
            LOGGER.warning("Action non reconnue pour ClientRentalServlet (POST): " + action + ". Redirection par défaut.");
            response.sendRedirect(request.getContextPath() + "/clientDashboard?tab=cars");
        }
    }

    // Méthode utilitaire pour repopuler le formulaire en cas d'erreur
    private void repopulateClientCardForm(HttpServletRequest request, String immatriculationVoiture, String dateDebutStr, String nombreJoursStr, String montantTotalEstimeClean) {
        Voiture voiture = voitureService.getVoitureByImmatriculation(immatriculationVoiture);
        if (voiture != null) {
            request.setAttribute("selectedVoiture", voiture);
        }

        // Utilisation du DTO RentalFormData avec la valeur numérique propre
        RentalFormData formData = new RentalFormData(dateDebutStr, nombreJoursStr, montantTotalEstimeClean);
        request.setAttribute("formData", formData);
    }
}