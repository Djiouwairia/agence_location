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
    "/clientVoitures/*", "/clientRental/*", // Servlets des clients
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
    // CORRECTION MAJEURE ICI: doFilter doit être public pour implémenter l'interface Filter
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        LOGGER.info("AuthFilter traitant l'URI de la requête : " + httpRequest.getRequestURI());

        HttpSession session = httpRequest.getSession(false);

        // Déterminer l'état de connexion et le rôle de manière robuste
        boolean isStaffLoggedIn = false;
        String staffRole = null;
        if (session != null && session.getAttribute("utilisateur") != null) {
            staffRole = (String) session.getAttribute("role");
            // S'assurer que le rôle est bien un rôle de personnel reconnu
            if ("Gestionnaire".equals(staffRole) || "ChefAgence".equals(staffRole)) {
                isStaffLoggedIn = true;
            }
        }

        boolean isClientLoggedIn = false;
        // Correction: Vérifier l'attribut "client" ET le rôle "Client"
        if (session != null && session.getAttribute("client") != null && "Client".equals(session.getAttribute("role"))) {
            isClientLoggedIn = true;
        }
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();

        // Chemins autorisés sans authentification
        String loginStaffURI = contextPath + "/login.jsp"; // Page de connexion du personnel
        String loginClientURI = contextPath + "/login.jsp?client=true"; // Page de connexion client (même page, paramètre différent)
        String authServletStaffURI = contextPath + "/auth"; // Servlet d'authentification du personnel
        String authServletClientURI = contextPath + "/clientAuth"; // Servlet d'authentification client

        // Vérifier si la requête est pour une ressource statique (CSS, JS, images)
        boolean isStaticResource = requestURI.startsWith(contextPath + "/css/") ||
                                   requestURI.startsWith(contextPath + "/js/") ||
                                   requestURI.startsWith(contextPath + "/images/"); // Assurez-vous d'ajouter votre dossier d'images si existant

        // === LOGIQUE D'AUTORISATION ===

        // 1. Autoriser les ressources statiques et les pages de connexion/servlets d'authentification
        if (isStaticResource || requestURI.equals(loginStaffURI) || requestURI.equals(loginClientURI) ||
            requestURI.equals(authServletStaffURI) || requestURI.equals(authServletClientURI) ||
            requestURI.equals(contextPath + "/index.jsp")) { // Permettre l'accès à index.jsp
            chain.doFilter(request, response);
            return; // Arrêter le traitement du filtre ici
        }

        // 2. Traitement des accès protégés
        // Chemins relatifs à l'application du personnel (servlets ou JSPs sous WEB-INF/views/)
        boolean isStaffProtectedPath = requestURI.startsWith(contextPath + "/dashboard") ||
                                       requestURI.startsWith(contextPath + "/clients") ||
                                       requestURI.startsWith(contextPath + "/voitures") ||
                                       requestURI.startsWith(contextPath + "/locations") ||
                                       requestURI.startsWith(contextPath + "/reports") ||
                                       requestURI.equals(contextPath + "/WEB-INF/views/chefDashboard.jsp") ||
                                       requestURI.equals(contextPath + "/WEB-INF/views/gestionnaireDashboard.jsp") ||
                                       // Ajout explicite des JSPs du personnel ou génériques pour le personnel
                                       requestURI.equals(contextPath + "/WEB-INF/views/clientForm.jsp") ||
                                       requestURI.equals(contextPath + "/WEB-INF/views/locationForm.jsp") ||
                                       requestURI.equals(contextPath + "/WEB-INF/views/locationList.jsp") ||
                                       requestURI.equals(contextPath + "/WEB-INF/views/returnCarForm.jsp") ||
                                       requestURI.equals(contextPath + "/WEB-INF/views/voitureForm.jsp") ||
                                       requestURI.equals(contextPath + "/WEB-INF/views/voitureList.jsp");


        // Chemins protégés pour les clients (servlets ou JSPs sous WEB-INF/views/)
        boolean isClientProtectedPath = requestURI.startsWith(contextPath + "/clientVoitures") ||
                                        requestURI.startsWith(contextPath + "/clientRental") ||
                                        requestURI.equals(contextPath + "/WEB-INF/views/clientDashboard.jsp") ||
                                        requestURI.equals(contextPath + "/WEB-INF/views/clientCard.jsp");

        // Logique d'accès
        if (isStaffProtectedPath) {
            if (isStaffLoggedIn) {
                LOGGER.info("Personnel (" + staffRole + ") connecté, accès autorisé à: " + requestURI);
                chain.doFilter(request, response);
            } else {
                LOGGER.warning("Accès non autorisé au personnel (non connecté) à: " + requestURI);
                httpResponse.sendRedirect(loginStaffURI);
            }
        } else if (isClientProtectedPath) {
            if (isClientLoggedIn) {
                LOGGER.info("Client connecté, accès autorisé à: " + requestURI);
                chain.doFilter(request, response);
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
