package com.agence.location.servlet;

import com.agence.location.service.VoitureService;
import com.agence.location.model.Voiture;
import com.agence.location.model.Utilisateur;

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
 * Servlet pour gérer les opérations CRUD sur les voitures.
 * Délègue la logique métier à VoitureService.
 */
@WebServlet("/voitures")
public class VoitureServlet extends HttpServlet {

    private VoitureService voitureService;

    @Override
    public void init() throws ServletException {
        super.init();
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
                // Lors de l'affichage du formulaire pour une nouvelle voiture,
                // on ne met pas d'attribut 'voiture' pour que le formulaire passe en mode 'ajout'.
                request.getRequestDispatcher("/WEB-INF/views/voitureForm.jsp").forward(request, response);
                break;
            case "edit":
                String immatToEdit = request.getParameter("immatriculation");
                Voiture voitureToEdit = voitureService.getVoitureByImmatriculation(immatToEdit);
                request.setAttribute("voiture", voitureToEdit);
                request.getRequestDispatcher("/WEB-INF/views/voitureForm.jsp").forward(request, response);
                break;
            case "delete":
                String immatToDelete = request.getParameter("immatriculation");
                try {
                    voitureService.deleteVoiture(immatToDelete);
                    request.setAttribute("message", "Voiture supprimée avec succès !");
                } catch (RuntimeException e) {
                    request.setAttribute("error", "Erreur lors de la suppression de la voiture : " + e.getMessage());
                }
                response.sendRedirect("voitures?action=list");
                break;
            case "search":
                String marque = request.getParameter("marque");
                Double kilometrageMax = null;
                try {
                    String kmStr = request.getParameter("kilometrageMax");
                    if (kmStr != null && !kmStr.trim().isEmpty()) {
                        kilometrageMax = Double.parseDouble(kmStr);
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "Format de kilométrage invalide.");
                }

                Integer anneeMiseCirculationMin = null;
                try {
                    String anneeStr = request.getParameter("anneeMiseCirculationMin");
                    if (anneeStr != null && !anneeStr.trim().isEmpty()) {
                        anneeMiseCirculationMin = Integer.parseInt(anneeStr);
                    }
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "Format d'année invalide.");
                }

                String typeCarburant = request.getParameter("typeCarburant");
                String categorie = request.getParameter("categorie");
                String statutSearch = request.getParameter("statut"); // Renommé pour éviter conflit

                List<Voiture> searchResults = voitureService.searchVoitures(marque, kilometrageMax, anneeMiseCirculationMin, typeCarburant, categorie, statutSearch);
                request.setAttribute("voitures", searchResults);
                request.getRequestDispatcher("/WEB-INF/views/voitureList.jsp").forward(request, response);
                break;
            case "list":
            default:
                List<Voiture> voitures = voitureService.getAllVoitures();
                request.setAttribute("voitures", voitures);
                request.getRequestDispatcher("/WEB-INF/views/voitureList.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // NOUVEAU LOG DE DÉBOGAGE : Vérifiez si cette ligne apparaît dans la console.
        System.out.println("VoitureServlet: Méthode doPost appelée pour l'URL: " + request.getRequestURI() + " avec action: " + request.getParameter("action"));

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utilisateur") == null || !"Gestionnaire".equals(session.getAttribute("role"))) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String immatriculation = request.getParameter("immatriculation");
        String nbPlacesStr = request.getParameter("nbPlaces");
        String marque = request.getParameter("marque");
        String modele = request.getParameter("modele");
        String dateMiseCirculationStr = request.getParameter("dateMiseCirculation");
        String kilometrageStr = request.getParameter("kilometrage");
        String typeCarburant = request.getParameter("typeCarburant");
        String categorie = request.getParameter("categorie");
        String prixLocationJStr = request.getParameter("prixLocationJ");
        String statut = request.getParameter("statut"); // Ce sera null si le champ n'est pas envoyé par le formulaire (mode 'add')

        // Gérer le cas où 'statut' est null (pour les nouvelles voitures)
        if (statut == null || statut.trim().isEmpty()) {
            statut = "Disponible"; // Valeur par défaut si non fournie par le formulaire
        }

        int nbPlaces = 0;
        LocalDate dateMiseCirculation = null;
        double kilometrage = 0.0;
        int prixLocationJ = 0;

        try {
            nbPlaces = Integer.parseInt(nbPlacesStr);
            dateMiseCirculation = LocalDate.parse(dateMiseCirculationStr);
            kilometrage = Double.parseDouble(kilometrageStr);
            prixLocationJ = Integer.parseInt(prixLocationJStr);
        } catch (NumberFormatException | DateTimeParseException e) {
            System.err.println("VoitureServlet: Erreur de conversion de données : " + e.getMessage());
            e.printStackTrace(); // Affiche la stack trace complète
            request.setAttribute("error", "Erreur de format de données pour les nombres ou la date : " + e.getMessage());
            // Important : remettre les données du formulaire pour ne pas les perdre
            Voiture tempVoiture = new Voiture(immatriculation, nbPlaces, marque, modele,
                                             dateMiseCirculation, kilometrage, typeCarburant,
                                             categorie, prixLocationJ, statut); // Utilise la valeur par défaut ou celle du formulaire pour statut
            request.setAttribute("voiture", tempVoiture);
            request.getRequestDispatcher("/WEB-INF/views/voitureForm.jsp").forward(request, response);
            return;
        }

        Voiture voiture = new Voiture(immatriculation, nbPlaces, marque, modele,
                                     dateMiseCirculation, kilometrage, typeCarburant,
                                     categorie, prixLocationJ, statut); // 'statut' est maintenant garanti non null

        System.out.println("VoitureServlet: Objet Voiture créé: " + voiture.toString());

        try {
            if ("add".equals(action)) {
                System.out.println("VoitureServlet: Tentative d'ajout de la voiture.");
                voitureService.addVoiture(voiture);
                request.setAttribute("message", "Voiture ajoutée avec succès !");
            } else if ("update".equals(action)) {
                System.out.println("VoitureServlet: Tentative de mise à jour de la voiture.");
                voitureService.updateVoiture(voiture);
                request.setAttribute("message", "Voiture mise à jour avec succès !");
            }
        } catch (RuntimeException e) {
            System.err.println("VoitureServlet: Erreur lors de l'opération sur la voiture : " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de l'opération sur la voiture : " + e.getMessage());
            request.setAttribute("voiture", voiture);
            request.getRequestDispatcher("/WEB-INF/views/voitureForm.jsp").forward(request, response);
            return;
        }

        System.out.println("VoitureServlet: Redirection vers la liste des voitures.");
        response.sendRedirect("voitures?action=list");
    }
}
