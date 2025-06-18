<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Liste des Locations</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="dashboard-container">
        <h2 class="text-2xl font-bold mb-6">Gestion des Locations</h2>

        <%-- Message de succès depuis la session --%>
        <c:if test="${not empty sessionScope.message}">
            <p class="success-message">${sessionScope.message}</p>
            <c:remove var="message" scope="session"/> <%-- Supprime le message après affichage --%>
        </c:if>
        <%-- Message d'erreur depuis la session --%>
        <c:if test="${not empty sessionScope.error}">
            <p class="error-message">${sessionScope.error}</p>
            <c:remove var="error" scope="session"/> <%-- Supprime l'erreur après affichage --%>
        </c:if>

        <div class="mb-4">
            <a href="locations?action=new" class="inline-block bg-green-600 text-white py-2 px-4 rounded-md hover:bg-green-700">Enregistrer une nouvelle location</a>
        </div>

        <c:choose>
            <c:when test="${not empty requestScope.locations}">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Client (CIN)</th>
                            <th>Voiture (Immat.)</th>
                            <th>Gestionnaire</th>
                            <th>Date Début</th>
                            <th>Jours</th>
                            <th>Date Retour Prévue</th>
                            <th>Date Retour Réelle</th>
                            <th>Montant Total</th>
                            <th>Statut</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="location" items="${requestScope.locations}">
                            <tr>
                                <td>${location.id}</td>
                                <td>${location.client.cin} - ${location.client.nom}</td>
                                <td>${location.voiture.immatriculation} - ${location.voiture.marque}</td>
                                <td>${location.utilisateur.nom}</td>
                                <%-- MODIFICATION ICI : Utilisation de getLegacyDateDebut() --%>
                                <td><fmt:formatDate value="${location.legacyDateDebut}" pattern="dd/MM/yyyy" /></td>
                                <td>${location.nombreJours}</td>
                                <%-- MODIFICATION ICI : Utilisation de getLegacyDateRetourPrevue() --%>
                                <td><fmt:formatDate value="${location.legacyDateRetourPrevue}" pattern="dd/MM/yyyy" /></td>
                                <td>
                                    <%-- MODIFICATION ICI : Utilisation de getLegacyDateRetourReelle() --%>
                                    <c:if test="${location.legacyDateRetourReelle != null}">
                                        <fmt:formatDate value="${location.legacyDateRetourReelle}" pattern="dd/MM/yyyy" />
                                    </c:if>
                                    <c:if test="${location.legacyDateRetourReelle == null}">
                                        N/A
                                    </c:if>
                                </td>
                                <td><fmt:formatNumber value="${location.montantTotal}" type="currency" currencySymbol="€" /></td>
                                <td>${location.statut}</td>
                                <td>
                                    <c:if test="${location.statut eq 'En cours'}">
                                        <a href="locations?action=return&id=${location.id}" class="text-yellow-600 hover:underline mr-2">Retourner</a>
                                    </c:if>
                                    <c:if test="${location.statut eq 'Terminee'}">
                                        <a href="locations?action=generateInvoice&locationId=${location.id}" class="text-purple-600 hover:underline">Facture PDF</a>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p>Aucune location enregistrée.</p>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>