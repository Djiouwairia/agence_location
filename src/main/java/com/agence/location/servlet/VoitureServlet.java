package com.agence.location.servlet;

import com.agence.location.dao.VoitureDAO;
import com.agence.location.model.Voiture;
import com.agence.location.model.Utilisateur; // Pour vérifier le rôle

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
 * Mappée à l'URL /voitures.
 */
@WebServlet("/voitures")
public class VoitureServlet extends HttpServlet {

    private VoitureDAO voitureDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        voitureDAO = new VoitureDAO();
    }

    /**
     * Gère les requêtes GET pour afficher la liste des voitures ou un formulaire d'édition.
     * @param request L'objet HttpServletRequest.
     * @param response L'objet HttpServletResponse.
     * @throws ServletException Si une erreur de servlet survient.
     * @throws IOException Si une erreur d'entrée/sortie survient.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Vérification de l'authentification et du rôle (seul le gestionnaire peut gérer les voitures)
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
                // Affiche le formulaire pour une nouvelle voiture
                request.getRequestDispatcher("/WEB-INF/views/voitureForm.jsp").forward(request, response);
                break;
            case "edit":
                // Affiche le formulaire pour modifier une voiture existante
                String immatToEdit = request.getParameter("immatriculation");
                Voiture voitureToEdit = voitureDAO.findById(immatToEdit);
                request.setAttribute("voiture", voitureToEdit);
                request.getRequestDispatcher("/WEB-INF/views/voitureForm.jsp").forward(request, response);
                break;
            case "delete":
                // Supprime une voiture
                String immatToDelete = request.getParameter("immatriculation");
                Voiture voitureToDelete = voitureDAO.findById(immatToDelete);
                if (voitureToDelete != null) {
                    try {
                        voitureDAO.delete(voitureToDelete);
                        request.setAttribute("message", "Voiture supprimée avec succès !");
                    } catch (RuntimeException e) {
                        request.setAttribute("error", "Erreur lors de la suppression de la voiture : " + e.getMessage());
                    }
                } else {
                    request.setAttribute("error", "Voiture non trouvée pour suppression.");
                }
                // Après suppression, redirige vers la liste
                response.sendRedirect("voitures?action=list");
                break;
            case "search":
                // Récupère les critères de recherche
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
                String statut = request.getParameter("statut");

                List<Voiture> searchResults = voitureDAO.searchVoitures(marque, kilometrageMax, anneeMiseCirculationMin, typeCarburant, categorie, statut);
                request.setAttribute("voitures", searchResults);
                request.getRequestDispatcher("/WEB-INF/views/voitureList.jsp").forward(request, response);
                break;
            case "list":
            default:
                // Affiche la liste de toutes les voitures
                List<Voiture> voitures = voitureDAO.findAll();
                request.setAttribute("voitures", voitures);
                request.getRequestDispatcher("/WEB-INF/views/voitureList.jsp").forward(request, response);
                break;
        }
    }

    /**
     * Gère les requêtes POST pour soumettre les données des formulaires (ajout/modification).
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
        String immatriculation = request.getParameter("immatriculation");
        String nbPlacesStr = request.getParameter("nbPlaces");
        String marque = request.getParameter("marque");
        String modele = request.getParameter("modele");
        String dateMiseCirculationStr = request.getParameter("dateMiseCirculation");
        String kilometrageStr = request.getParameter("kilometrage");
        String typeCarburant = request.getParameter("typeCarburant");
        String categorie = request.getParameter("categorie");
        String prixLocationJStr = request.getParameter("prixLocationJ");
        String statut = request.getParameter("statut");

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
            request.setAttribute("error", "Erreur de format de données pour les nombres ou la date : " + e.getMessage());
            // Important : remettre les données du formulaire pour ne pas les perdre
            Voiture tempVoiture = new Voiture(immatriculation, nbPlaces, marque, modele,
                                             dateMiseCirculation, kilometrage, typeCarburant,
                                             categorie, prixLocationJ, statut);
            request.setAttribute("voiture", tempVoiture);
            request.getRequestDispatcher("/WEB-INF/views/voitureForm.jsp").forward(request, response);
            return;
        }

        Voiture voiture = new Voiture(immatriculation, nbPlaces, marque, modele,
                                     dateMiseCirculation, kilometrage, typeCarburant,
                                     categorie, prixLocationJ, statut);

        try {
            if ("add".equals(action)) {
                // Vérifie si une voiture avec la même immatriculation existe déjà
                if (voitureDAO.findById(immatriculation) != null) {
                    request.setAttribute("error", "Une voiture avec cette immatriculation existe déjà.");
                    request.setAttribute("voiture", voiture); // Remettre les données du formulaire
                    request.getRequestDispatcher("/WEB-INF/views/voitureForm.jsp").forward(request, response);
                    return;
                }
                voitureDAO.save(voiture);
                request.setAttribute("message", "Voiture ajoutée avec succès !");
            } else if ("update".equals(action)) {
                // Récupère la voiture existante pour s'assurer qu'elle existe avant la mise à jour
                Voiture existingVoiture = voitureDAO.findById(immatriculation);
                if (existingVoiture != null) {
                    // Met à jour les champs de l'objet existant
                    existingVoiture.setNbPlaces(nbPlaces);
                    existingVoiture.setMarque(marque);
                    existingVoiture.setModele(modele);
                    existingVoiture.setDateMiseCirculation(dateMiseCirculation);
                    existingVoiture.setKilometrage(kilometrage);
                    existingVoiture.setTypeCarburant(typeCarburant);
                    existingVoiture.setCategorie(categorie);
                    existingVoiture.setPrixLocationJ(prixLocationJ);
                    existingVoiture.setStatut(statut);
                    voitureDAO.save(existingVoiture); // Utilise save() qui gère aussi l'update
                    request.setAttribute("message", "Voiture mise à jour avec succès !");
                } else {
                    request.setAttribute("error", "Voiture non trouvée pour la mise à jour.");
                    request.setAttribute("voiture", voiture);
                    request.getRequestDispatcher("/WEB-INF/views/voitureForm.jsp").forward(request, response);
                    return;
                }
            }
        } catch (RuntimeException e) {
            request.setAttribute("error", "Erreur lors de l'opération sur la voiture : " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("voiture", voiture); // Repopulate form
            request.getRequestDispatcher("/WEB-INF/views/voitureForm.jsp").forward(request, response);
            return;
        }

        // Redirige toujours vers la liste après une opération réussie
        response.sendRedirect("voitures?action=list");
    }
}
