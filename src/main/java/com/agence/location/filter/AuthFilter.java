package com.agence.location.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Filtre d'authentification pour sécuriser les pages de l'application.
 * Il s'assure qu'un utilisateur (personnel ou client) est connecté avant d'accéder à certaines ressources.
 */
@WebFilter(urlPatterns = {
    "/dashboard/*", "/clients/*", "/voitures/*", "/locations/*", "/reports/*", // Servlets du personnel
    "/clientVoituresList/*", "/clientRental/*", "/clientDashboard/*", "/clientProfile/*", // Servlets des clients (AJOUTÉ /clientDashboard/* et /clientProfile/*)
    "/WEB-INF/views/*" // Protège TOUS les JSPs directement sous WEB-INF/views/
    // Les JSPs spécifiques à chaque rôle seront gérées dans la logique du doFilter
})
public class AuthFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(AuthFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("AuthFilter initialisé.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false); // Ne crée pas de nouvelle session si elle n'existe pas

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        LOGGER.info("AuthFilter traitant l'URI de la requête : " + requestURI);

        // Chemins publics (accessibles sans authentification)
        boolean isPublicResource = requestURI.equals(contextPath + "/login.jsp") ||
                                   requestURI.equals(contextPath + "/auth") ||
                                   requestURI.equals(contextPath + "/clientAuth") ||     // AJOUTÉ
                                   requestURI.equals(contextPath + "/clientRegister.jsp") || // Si vous avez une page d'inscription client
                                   requestURI.startsWith(contextPath + "/css/") ||
                                   requestURI.startsWith(contextPath + "/js/") ||
                                   requestURI.startsWith(contextPath + "/images/");

        if (isPublicResource) {
            LOGGER.info("Accès autorisé à une ressource publique : " + requestURI);
            chain.doFilter(request, response);
            return;
        }

        // Vérifier si l'utilisateur est connecté et quel est son rôle
        boolean isStaffLoggedIn = (session != null && session.getAttribute("utilisateur") != null && ("Gestionnaire".equals(session.getAttribute("role")) || "ChefAgence".equals(session.getAttribute("role"))));
        boolean isClientLoggedIn = (session != null && session.getAttribute("client") != null && "Client".equals(session.getAttribute("role")));

        // Définir les URIs de redirection
        String loginStaffURI = contextPath + "/login.jsp";
        String loginClientURI = contextPath + "/login.jsp";
        String dashboardStaffURI = contextPath + "/dashboard";
        String dashboardClientURI = contextPath + "/clientDashboard"; // AJOUTÉ

        // Logique de redirection basée sur le rôle et l'URI demandée
        if (requestURI.startsWith(contextPath + "/dashboard") ||
            requestURI.startsWith(contextPath + "/clients") ||
            requestURI.startsWith(contextPath + "/voitures") ||
            requestURI.startsWith(contextPath + "/locations") ||
            requestURI.startsWith(contextPath + "/reports") ||
            requestURI.startsWith(contextPath + "/api/")) { // Protège aussi les APIs du personnel
            
            // C'est une ressource du personnel
            if (isStaffLoggedIn) {
                LOGGER.info("Accès autorisé au personnel connecté à: " + requestURI);
                chain.doFilter(request, response);
            } else if (isClientLoggedIn) {
                // Un client essaie d'accéder à une ressource du personnel
                LOGGER.warning("Accès non autorisé au client (connecté) à une ressource personnel: " + requestURI + ". Redirection vers le tableau de bord client.");
                httpResponse.sendRedirect(dashboardClientURI); // Redirige le client vers son propre tableau de bord
            } else {
                LOGGER.warning("Accès non autorisé au personnel (non connecté) à: " + requestURI);
                httpResponse.sendRedirect(loginStaffURI);
            }
        } else if (requestURI.startsWith(contextPath + "/clientVoituresList") ||
                   requestURI.startsWith(contextPath + "/clientRental") ||
                   requestURI.startsWith(contextPath + "/clientDashboard") || // AJOUTÉ
                   requestURI.startsWith(contextPath + "/clientProfile") ||   // AJOUTÉ
                   requestURI.startsWith(contextPath + "/WEB-INF/views/")) { // Protège les JSPs clients sous WEB-INF
            
            // C'est une ressource client
            if (isClientLoggedIn) {
                LOGGER.info("Client connecté, accès autorisé à: " + requestURI);
                chain.doFilter(request, response);
            } else if (isStaffLoggedIn) {
                 // Un membre du personnel essaie d'accéder à une ressource client
                LOGGER.warning("Accès non autorisé au personnel (connecté) à une ressource client: " + requestURI + ". Redirection vers le tableau de bord personnel.");
                httpResponse.sendRedirect(dashboardStaffURI); // Redirige le personnel vers son propre tableau de bord
            } else {
                LOGGER.warning("Accès non autorisé au client (non connecté) à: " + requestURI);
                httpResponse.sendRedirect(loginClientURI);
            }
        } else {
            // Cas par défaut : chemin non spécifiquement protégé par les règles ci-dessus.
            // Cela peut arriver si l'utilisateur essaie d'accéder à une ressource non mappée
            // ou à une ressource commune (comme navbar.jsp s'il était accédé directement, bien que normalement inclus).
            // Si un utilisateur est connecté (personnel ou client), on autorise.
            if (isStaffLoggedIn || isClientLoggedIn) {
                LOGGER.info("AuthFilter: Chemin non spécifiquement protégé, mais utilisateur connecté. Autorisation par défaut : " + requestURI);
                chain.doFilter(request, response);
            } else {
                // Si le chemin n'est pas public et personne n'est connecté, rediriger vers la page de login par défaut.
                LOGGER.warning("AuthFilter: Chemin non spécifiquement protégé et aucun utilisateur connecté. Redirection par défaut : " + requestURI);
                httpResponse.sendRedirect(loginStaffURI); // Redirection par défaut vers le login personnel
            }
        }
    }

    @Override
    public void destroy() {
        LOGGER.info("AuthFilter détruit.");
    }
}
