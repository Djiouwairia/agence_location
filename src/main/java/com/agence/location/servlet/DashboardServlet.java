package com.agence.location.servlet;

import com.agence.location.service.ReportService;
import com.agence.location.model.Utilisateur;
import com.agence.location.model.Location; // Pour obtenir les informations sur les locataires

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Servlet pour afficher les tableaux de bord du chef d'agence et des gestionnaires.
 * Délègue l'obtention des données de rapport à ReportService.
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private ReportService reportService;

    @Override
    public void init() throws ServletException {
        super.init();
        reportService = new ReportService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("utilisateur") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateur");
        String role = utilisateur.getRole();

        // Récupération des données du parking via ReportService pour les deux rôles
        request.setAttribute("nombreTotalVoitures", reportService.getTotalNumberOfCars());
        request.setAttribute("nombreVoituresDisponibles", reportService.getNumberOfAvailableCars());
        request.setAttribute("nombreVoituresLouees", reportService.getNumberOfRentedCars());
        request.setAttribute("voituresLoueesAvecInfosLocataires", reportService.getRentedCarsWithTenantInfo());

        if ("ChefAgence".equals(role)) {
            // Fonctionnalités spécifiques au chef d'agence via ReportService
            request.setAttribute("voituresPlusRecherches", reportService.getMostSearchedCars(5)); // Top 5

            LocalDate now = LocalDate.now();
            request.setAttribute("bilanMensuel", reportService.getMonthlyFinancialReport(now.getYear(), now.getMonthValue()));
            request.setAttribute("moisBilan", now.getMonth().name());
            request.setAttribute("anneeBilan", now.getYear());

            request.getRequestDispatcher("/WEB-INF/views/chefDashboard.jsp").forward(request, response);
        } else if ("Gestionnaire".equals(role)) {
            // Le gestionnaire utilise les mêmes données de parking.
            request.getRequestDispatcher("/WEB-INF/views/gestionnaireDashboard.jsp").forward(request, response);
        } else {
            session.invalidate();
            request.setAttribute("error", "Accès non autorisé ou rôle inconnu.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
