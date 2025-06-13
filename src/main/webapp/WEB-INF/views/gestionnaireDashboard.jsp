<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.agence.location.model.Location" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tableau de Bord Gestionnaire</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="dashboard-container">
        <h2 class="text-2xl font-bold mb-6">Tableau de Bord du Gestionnaire</h2>

        <%-- Section État du Parking (similaire à celle du chef) --%>
        <div class="dashboard-section">
            <h3>État du Parking</h3>
            <p>Nombre total de voitures : <strong>${requestScope.nombreTotalVoitures}</strong></p>
            <p>Voitures disponibles : <strong>${requestScope.nombreVoituresDisponibles}</strong></p>
            <p>Voitures louées : <strong>${requestScope.nombreVoituresLouees}</strong></p>

            <h4>Liste des voitures en location (avec locataires)</h4>
            <c:choose>
                <c:when test="${not empty requestScope.voituresLoueesAvecInfosLocataires}">
                    <table>
                        <thead>
                            <tr>
                                <th>Immatriculation</th>
                                <th>Marque</th>
                                <th>Modèle</th>
                                <th>Locataire (CIN)</th>
                                <th>Locataire (Nom)</th>
                                <th>Date Début</th>
                                <th>Jours</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="location" items="${requestScope.voituresLoueesAvecInfosLocataires}">
                                <tr>
                                    <td>${location.voiture.immatriculation}</td>
                                    <td>${location.voiture.marque}</td>
                                    <td>${location.voiture.modele}</td>
                                    <td>${location.client.cin}</td>
                                    <td>${location.client.prenom} ${location.client.nom}</td>
                                    <td>${location.dateDebut}</td>
                                    <td>${location.nombreJours}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <p>Aucune voiture actuellement en location.</p>
                </c:otherwise>
            </c:choose>
        </div>

        <%-- Le gestionnaire peut avoir d'autres sections ici si des fonctionnalités supplémentaires sont ajoutées --%>
    </div>
</body>
</html>
