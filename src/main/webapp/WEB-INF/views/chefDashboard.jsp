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
    <title>Tableau de Bord Chef d'Agence</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="dashboard-container">
        <h2 class="text-2xl font-bold mb-6">Tableau de Bord du Chef d'Agence</h2>

        <%-- Section État du Parking --%>
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

        <%-- Section Voitures les plus recherchées --%>
        <div class="dashboard-section">
            <h3>Voitures les plus recherchées (Top 5 des plus louées)</h3>
            <c:choose>
                <c:when test="${not empty requestScope.voituresPlusRecherches}">
                    <table>
                        <thead>
                            <tr>
                                <th>Immatriculation</th>
                                <th>Marque</th>
                                <th>Modèle</th>
                                <th>Nombre de locations</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${requestScope.voituresPlusRecherches}">
                                <tr>
                                    <td>${item[0].immatriculation}</td>
                                    <td>${item[0].marque}</td>
                                    <td>${item[0].modele}</td>
                                    <td>${item[1]}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <p>Aucune donnée sur les voitures les plus recherchées pour le moment.</p>
                </c:otherwise>
            </c:choose>
        </div>

        <%-- Section Bilan Financier Mensuel --%>
        <div class="dashboard-section">
            <h3>Bilan Financier Mensuel (${requestScope.moisBilan} ${requestScope.anneeBilan})</h3>
            <p>Montant total des locations terminées ce mois-ci : <strong>${requestScope.bilanMensuel} €</strong></p>
            <p class="text-sm text-gray-500">Note: Ce bilan inclut les locations dont le retour réel a eu lieu ce mois-ci.</p>
        </div>

    </div>
</body>
</html>
