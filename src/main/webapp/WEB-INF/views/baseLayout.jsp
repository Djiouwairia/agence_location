<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${param.pageTitle != null ? param.pageTitle : 'Agence de Location'}</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <%-- Script Chart.js pour les graphiques, inclus ici pour être disponible globalement si besoin --%>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <%-- Le main.js pourrait être inclus ici ou dans les pages spécifiques si sa logique est localisée --%>
    <script src="js/main.js"></script>
</head>
<body class="app-container">
    <%-- Inclusion de la barre latérale (qui est maintenant navbar.jsp) --%>
    <jsp:include page="navbar.jsp"/>

    <%-- Conteneur principal du contenu --%>
    <div class="main-content">
        <%-- Ce message est pour les pages qui pourraient avoir un message d'erreur/succès au niveau global --%>
        <c:if test="${not empty requestScope.message}">
            <p class="success-message">${requestScope.message}</p>
        </c:if>
        <c:if test="${not empty requestScope.error}">
            <p class="error-message">${requestScope.error}</p>
        </c:if>

        <%-- Ici sera inclus le contenu spécifique de chaque page JSP --%>
        <jsp:doBody/>
    </div>
</body>
</html>
