package com.agence.location.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filtre d'authentification pour sécuriser les pages de l'application.
 * Il s'assure qu'un utilisateur est connecté avant d'accéder à certaines ressources.
 */
@WebFilter(urlPatterns = {"/dashboard/*", "/clients/*", "/voitures/*", "/locations/*", "/reports/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialisation du filtre, si nécessaire
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false); // Ne crée pas de nouvelle session si elle n'existe pas

        boolean isLoggedIn = (session != null && session.getAttribute("utilisateur") != null);
        String loginURI = httpRequest.getContextPath() + "/login.jsp"; // Chemin vers la page de connexion
        String authServletURI = httpRequest.getContextPath() + "/auth"; // Chemin vers la servlet d'authentification

        boolean isLoginRequest = httpRequest.getRequestURI().equals(loginURI);
        boolean isAuthServlet = httpRequest.getRequestURI().equals(authServletURI);
        boolean isStaticResource = httpRequest.getRequestURI().startsWith(httpRequest.getContextPath() + "/css/") ||
                                   httpRequest.getRequestURI().startsWith(httpRequest.getContextPath() + "/js/");

        if (isLoggedIn || isLoginRequest || isAuthServlet || isStaticResource) {
            // L'utilisateur est connecté, ou c'est la page de connexion, la servlet d'auth, ou une ressource statique.
            // Laisse la requête continuer sa route.
            chain.doFilter(request, response);
        } else {
            // L'utilisateur n'est pas connecté et essaie d'accéder à une ressource protégée.
            // Redirige vers la page de connexion.
            httpResponse.sendRedirect(loginURI);
        }
    }

    @Override
    public void destroy() {
        // Nettoyage du filtre, si nécessaire
    }
}
