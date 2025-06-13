package com.agence.location.servlet;

import com.agence.location.dao.LocationDAO;
import com.agence.location.dao.VoitureDAO;
import com.agence.location.model.Utilisateur;
import com.agence.location.model.Voiture;
import com.agence.location.model.Location; // Importez Location pour obtenir les informations sur les locataires

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servlet pour afficher les tableaux de bord du chef d'agence et des gestionnaires.
 */
@WebServlet("/dashboard") // Mappe cette servlet à l'URL /dashboard
public class DashboardServlet extends HttpServlet {

    private VoitureDAO voitureDAO;
    private LocationDAO locationDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        voitureDAO = new VoitureDAO();
        locationDAO = new LocationDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Vérification de l'authentification
        if (session == null || session.getAttribute("utilisateur") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        String role = utilisateur.getRole(); // Ou (String) session.getAttribute("role");

        // Récupération de l'état du parking pour les deux rôles
        List<Voiture> toutesVoitures = voitureDAO.findAll();
        List<Voiture> voituresDisponibles = voitureDAO.getVoituresDisponibles();
        List<Voiture> voituresLouees = voitureDAO.getVoituresLouees();
        List<Location> locationsEnCours = locationDAO.getAllLocations().stream()
                .filter(l -> "En cours".equals(l.getStatut()))
                .collect(Collectors.toList());


        request.setAttribute("nombreTotalVoitures", toutesVoitures.size());
        request.setAttribute("nombreVoituresDisponibles", voituresDisponibles.size());
        request.setAttribute("nombreVoituresLouees", voituresLouees.size());
        request.setAttribute("voituresLoueesAvecInfosLocataires", locationsEnCours); // Liste des objets Location avec infos locataires

        if ("ChefAgence".equals(role)) {
            // Fonctionnalités spécifiques au chef d'agence
            // 1. Voitures les plus recherchées
            List<Object[]> voituresPlusRecherches = locationDAO.getVoituresLesPlusRecherches(5); // Top 5
            request.setAttribute("voituresPlusRecherches", voituresPlusRecherches);

            // 2. Bilan financier mensuel
            LocalDate now = LocalDate.now();
            int currentYear = now.getYear();
            int currentMonth = now.getMonthValue();
            double bilanMensuel = locationDAO.getBilanFinancierMensuel(currentYear, currentMonth);
            request.setAttribute("bilanMensuel", bilanMensuel);
            request.setAttribute("moisBilan", now.getMonth().name()); // Nom du mois en anglais par défaut
            request.setAttribute("anneeBilan", currentYear);


            request.getRequestDispatcher("/WEB-INF/views/chefDashboard.jsp").forward(request, response);
        } else if ("Gestionnaire".equals(role)) {
            // Fonctionnalités spécifiques au gestionnaire (état du parking déjà récupéré)
            request.getRequestDispatcher("/WEB-INF/views/gestionnaireDashboard.jsp").forward(request, response);
        } else {
            // Gérer le cas où le rôle n'est pas reconnu (redirection vers login avec erreur)
            session.invalidate(); // Invalide la session pour la sécurité
            request.setAttribute("error", "Accès non autorisé ou rôle inconnu.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
