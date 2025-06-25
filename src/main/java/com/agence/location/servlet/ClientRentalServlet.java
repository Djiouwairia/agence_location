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
        voitureService = new VoitureService();
        LOGGER.info("ClientRentalServlet initialisée.");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Vérification de l'authentification du client
        if (session == null || session.getAttribute("client") == null || !"Client".equals(session.getAttribute("role"))) {
            LOGGER.warning("Accès non autorisé à ClientRentalServlet (GET). Redirection vers la page de connexion client.");
            response.sendRedirect(request.getContextPath() + "/login.jsp?client=true");
            return;
        }

        Client client = (Client) session.getAttribute("client");
        String action = request.getParameter("action");

        if ("listMyRentals".equals(action)) {
            LOGGER.info("Action: listMyRentals - Récupération des locations pour le client: " + client.getCin());
            try {
                request.setAttribute("clientLocations", locationService.getLocationsByClient(client.getCin()));
                request.getRequestDispatcher("/WEB-INF/views/clientDashboard.jsp").forward(request, response);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la récupération des locations du client " + client.getCin() + " : " + e.getMessage(), e);
                request.setAttribute("error", "Erreur lors de la récupération de vos locations.");
                request.getRequestDispatcher("/WEB-INF/views/clientDashboard.jsp").forward(request, response);
            }
        } else if ("cancelRequest".equals(action)) {
            String locationIdStr = request.getParameter("id");
            if (locationIdStr != null && !locationIdStr.isEmpty()) {
                try {
                    Long locationId = Long.parseLong(locationIdStr);
                    Location location = locationService.getLocationByIdWithDetails(locationId);
                    if (location != null && location.getClient().getCin().equals(client.getCin()) && "En attente".equals(location.getStatut())) {
                        locationService.cancelRentalRequest(locationId);
                        session.setAttribute("message", "Demande de location annulée avec succès.");
                        LOGGER.info("Demande de location ID " + locationId + " annulée par le client " + client.getCin());
                    } else {
                        session.setAttribute("error", "Impossible d'annuler cette demande. Elle n'existe pas, ne vous appartient pas ou n'est plus en attente.");
                        LOGGER.warning("Tentative d'annulation échouée pour location ID " + locationId + " par client " + client.getCin());
                    }
                } catch (NumberFormatException e) {
                    session.setAttribute("error", "ID de demande invalide.");
                    LOGGER.log(Level.WARNING, "ID de demande invalide pour annulation: " + locationIdStr, e);
                } catch (RuntimeException e) {
                    session.setAttribute("error", "Erreur lors de l'annulation de la demande : " + e.getMessage());
                    LOGGER.log(Level.SEVERE, "Erreur Runtime lors de l'annulation de la demande ID " + locationIdStr + ": " + e.getMessage(), e);
                }
            } else {
                session.setAttribute("error", "ID de demande manquant.");
                LOGGER.warning("ID de demande manquant pour annulation.");
            }
            response.sendRedirect(request.getContextPath() + "/clientRental?action=listMyRentals");
        } else {
            LOGGER.warning("Action non reconnue pour ClientRentalServlet (GET): " + action + ". Redirection par défaut vers le dashboard client.");
            response.sendRedirect(request.getContextPath() + "/clientVoitures?action=listAvailable");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("client") == null || !"Client".equals(session.getAttribute("role"))) {
            LOGGER.warning("Accès non autorisé à ClientRentalServlet (POST). Redirection vers la page de connexion client.");
            response.sendRedirect(request.getContextPath() + "/login.jsp?client=true");
            return;
        }

        Client client = (Client) session.getAttribute("client");
        String action = request.getParameter("action");

        if ("request".equals(action)) {
            String immatriculationVoiture = request.getParameter("immatriculationVoiture");
            String dateDebutStr = request.getParameter("dateDebut");
            String nombreJoursStr = request.getParameter("nombreJours");
            String montantTotalEstimeRaw = request.getParameter("montantTotalEstime"); // Nom différent pour la chaîne brute

            LocalDate dateDebut = null;
            int nombreJours = 0;
            double montantTotalEstime = 0.0;
            String cleanMontantTotalEstime = ""; // Variable pour stocker la version propre pour repopuler

            try {
                dateDebut = LocalDate.parse(dateDebutStr);
                nombreJours = Integer.parseInt(nombreJoursStr);

                // Nettoyer la chaîne avant de la parser en Double
                // Remplacer la virgule par un point si la locale de la JSP utilise la virgule
                // Enlever tous les caractères non numériques, sauf le point décimal
                cleanMontantTotalEstime = montantTotalEstimeRaw.replace(" €", "").replace(",", "."); // Pour le format français
                // Assurez-vous qu'il ne reste que des chiffres et un point décimal
                montantTotalEstime = Double.parseDouble(cleanMontantTotalEstime);


                Voiture voiture = voitureService.getVoitureByImmatriculation(immatriculationVoiture);

                if (voiture == null || !"Disponible".equals(voiture.getStatut())) {
                    LOGGER.warning("Demande de location échouée: Voiture " + immatriculationVoiture + " non trouvée ou non disponible.");
                    throw new RuntimeException("La voiture n'est pas disponible pour la location.");
                }

                Location nouvelleDemande = new Location();
                nouvelleDemande.setClient(client);
                nouvelleDemande.setVoiture(voiture);
                nouvelleDemande.setUtilisateur(null);
                nouvelleDemande.setDateDebut(dateDebut);
                nouvelleDemande.setNombreJours(nombreJours);
                nouvelleDemande.setDateRetourPrevue(dateDebut.plusDays(nombreJours));
                nouvelleDemande.setMontantTotal(montantTotalEstime);
                nouvelleDemande.setKilometrageDepart(voiture.getKilometrage());
                nouvelleDemande.setKilometrageRetour(null);
                nouvelleDemande.setStatut("En attente");

                locationService.addRentalRequest(nouvelleDemande);
                session.setAttribute("message", "Votre demande de location a été soumise avec succès ! Elle est en attente de validation par l'agence.");
                LOGGER.info("Demande de location soumise par le client " + client.getCin() + " pour voiture " + immatriculationVoiture);
                response.sendRedirect(request.getContextPath() + "/clientRental?action=listMyRentals");

            } catch (DateTimeParseException e) {
                request.setAttribute("error", "Format de date invalide. Veuillez utiliser le format YYYY-MM-DD.");
                LOGGER.log(Level.WARNING, "Format de date invalide pour la demande de location: " + dateDebutStr, e);
                repopulateClientCardForm(request, immatriculationVoiture, dateDebutStr, nombreJoursStr, cleanMontantTotalEstime); // Passer la valeur propre
                request.getRequestDispatcher("/WEB-INF/views/clientCard.jsp").forward(request, response);
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Nombre de jours ou montant invalide. Veuillez entrer des nombres valides.");
                LOGGER.log(Level.WARNING, "Nombre de jours ou montant invalide: " + nombreJoursStr + ", " + montantTotalEstimeRaw, e);
                repopulateClientCardForm(request, immatriculationVoiture, dateDebutStr, nombreJoursStr, cleanMontantTotalEstime); // Passer la valeur propre
                request.getRequestDispatcher("/WEB-INF/views/clientCard.jsp").forward(request, response);
            } catch (RuntimeException e) {
                request.setAttribute("error", "Erreur lors de la soumission de la demande : " + e.getMessage());
                LOGGER.log(Level.SEVERE, "Erreur Runtime lors de la soumission de la demande: " + e.getMessage(), e);
                repopulateClientCardForm(request, immatriculationVoiture, dateDebutStr, nombreJoursStr, cleanMontantTotalEstime); // Passer la valeur propre
                request.getRequestDispatcher("/WEB-INF/views/clientCard.jsp").forward(request, response);
            } catch (Exception e) {
                request.setAttribute("error", "Une erreur inattendue est survenue : " + e.getClass().getName() + " - " + e.getMessage());
                LOGGER.log(Level.SEVERE, "Erreur inattendue dans doPost pour l'action 'request': " + e.getMessage(), e);
                repopulateClientCardForm(request, immatriculationVoiture, dateDebutStr, nombreJoursStr, cleanMontantTotalEstime); // Passer la valeur propre
                request.getRequestDispatcher("/WEB-INF/views/clientCard.jsp").forward(request, response);
            }
        } else {
            LOGGER.warning("Action non reconnue pour ClientRentalServlet (POST): " + action + ". Redirection par défaut.");
            response.sendRedirect(request.getContextPath() + "/clientVoitures?action=listAvailable");
        }
    }

    // Méthode utilitaire pour repopuler le formulaire en cas d'erreur
    private void repopulateClientCardForm(HttpServletRequest request, String immatriculationVoiture, String dateDebutStr, String nombreJoursStr, String montantTotalEstimeClean) {
        Voiture voiture = voitureService.getVoitureByImmatriculation(immatriculationVoiture);
        if (voiture != null) {
            request.setAttribute("voiture", voiture);
        }

        // Utilisation du DTO RentalFormData avec la valeur numérique propre
        RentalFormData formData = new RentalFormData(dateDebutStr, nombreJoursStr, montantTotalEstimeClean);
        request.setAttribute("formData", formData);
    }
}
