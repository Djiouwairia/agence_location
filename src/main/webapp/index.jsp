<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Accueil - Agence de Location de Voitures</title>
</head>
<body>
    <%-- Redirige vers la page de connexion si l'utilisateur n'est pas connect� --%>
    <% 
        if (session.getAttribute("utilisateur") == null) {
            response.sendRedirect("login.jsp");
            return; // Arr�te l'ex�cution de la JSP
        }
    %>
    <%-- Si connect�, redirige vers le tableau de bord appropri� --%>
    <% 
        String role = (String) session.getAttribute("role");
        if ("ChefAgence".equals(role)) {
            response.sendRedirect("dashboard?role=chef");
        } else if ("Gestionnaire".equals(role)) {
            response.sendRedirect("dashboard?role=gestionnaire");
        } else {
            // En cas de r�le inattendu, rediriger vers la page de connexion avec un message d'erreur
            response.sendRedirect("login.jsp?error=role_inconnu");
        }
    %>
</body>
</html>
