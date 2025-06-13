<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Accueil - Agence de Location de Voitures</title>
</head>
<body>
    <%-- Redirige vers la page de connexion si l'utilisateur n'est pas connecté --%>
    <% 
        if (session.getAttribute("utilisateur") == null) {
            response.sendRedirect("login.jsp");
            return; // Arrête l'exécution de la JSP
        }
    %>
    <%-- Si connecté, redirige vers le tableau de bord approprié --%>
    <% 
        String role = (String) session.getAttribute("role");
        if ("ChefAgence".equals(role)) {
            response.sendRedirect("dashboard?role=chef");
        } else if ("Gestionnaire".equals(role)) {
            response.sendRedirect("dashboard?role=gestionnaire");
        } else {
            // En cas de rôle inattendu, rediriger vers la page de connexion avec un message d'erreur
            response.sendRedirect("login.jsp?error=role_inconnu");
        }
    %>
</body>
</html>
