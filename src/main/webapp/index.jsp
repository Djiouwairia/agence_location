<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Accueil - Agence de Location de Voitures</title>
</head>
<body>
    <c:choose>
        <%-- Si un client est connecté --%>
        <c:when test="${not empty sessionScope.client}">
            <c:redirect url="/clientDashboard"/>
        </c:when>
        <%-- Si un utilisateur (personnel) est connecté --%>
        <c:when test="${not empty sessionScope.utilisateur}">
            <c:choose>
                <c:when test="${sessionScope.role eq 'ChefAgence'}">
                    <c:redirect url="/dashboard?role=chef"/>
                </c:when>
                <c:when test="${sessionScope.role eq 'Gestionnaire'}">
                    <c:redirect url="/dashboard?role=gestionnaire"/>
                </c:when>
                <c:otherwise>
                    <%-- Rôle inattendu pour le personnel, rediriger vers la page de connexion avec un message --%>
                    <c:redirect url="/login.jsp?error=role_inconnu"/>
                </c:otherwise>
            </c:choose>
        </c:when>
        <%-- Si personne n'est connecté, rediriger vers la page de connexion par défaut --%>
        <c:otherwise>
            <c:redirect url="/login.jsp"/>
        </c:otherwise>
    </c:choose>
</body>
</html>
