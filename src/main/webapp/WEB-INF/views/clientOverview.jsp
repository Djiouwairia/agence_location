<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%--
    Ce fichier JSP est un FRAGMENT de contenu pour l'onglet "Aperçu".
    Il est inclus dynamiquement dans clientDashboard.jsp.
    Il ne doit PAS contenir de balises <html>, <head>, <body>, <title> ou de liens <link> CSS.
    Ces éléments sont gérés par clientDashboard.jsp.
--%>

<h2 class="dashboard-heading text-3xl font-bold text-gray-800 mb-4 text-center">Bonjour <c:out value="${sessionScope.client.prenom}"/> !</h2>
<p class="text-lg text-gray-600 text-center mb-8">Bienvenue sur votre espace personnel. Gérez vos locations et trouvez votre prochaine voiture.</p>

<div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
    <!-- Carte: Mes Locations -->
    <div class="card dashboard-card cursor-pointer transition-transform hover:translate-y-[-5px] hover:shadow-xl" onclick="showTab('mesLocations');">
        <div class="flex flex-col items-center justify-center p-4">
            <div class="dashboard-card-icon text-blue-500 text-5xl mb-3">
                <i class="fas fa-clipboard-list"></i>
            </div>
            <h3 class="card-title text-xl font-semibold text-gray-700 mb-1">Mes Locations</h3>
            <p class="card-value text-blue-600 text-5xl font-bold mb-2">
                <c:out value="${requestScope.clientRentalsCount != null ? requestScope.clientRentalsCount : 'N/A'}"/>
            </p>
            <span class="text-sm text-gray-500 text-center">Demandes actives et passées</span>
        </div>
    </div>

    <!-- Carte: Nos Voitures Disponibles -->
    <div class="card dashboard-card cursor-pointer transition-transform hover:translate-y-[-5px] hover:shadow-xl" onclick="showTab('voituresDisponibles');">
        <div class="flex flex-col items-center justify-center p-4">
            <div class="dashboard-card-icon text-green-500 text-5xl mb-3">
                <i class="fas fa-car"></i>
            </div>
            <h3 class="card-title text-xl font-semibold text-gray-700 mb-1">Nos Voitures</h3>
            <p class="card-value text-green-600 text-5xl font-bold mb-2">
                <c:out value="${requestScope.availableCarsClientCount != null ? requestScope.availableCarsClientCount : 'N/A'}"/>
            </p>
            <span class="text-sm text-gray-500 text-center">Actuellement disponibles à la location</span>
        </div>
    </div>
</div>

<%-- Nouvelle section: Faire une nouvelle demande de location --%>
<div class="text-center mb-8">
    <a href="#" onclick="showTab('voituresDisponibles'); return false;" class="btn-primary text-xl px-8 py-4 rounded-full shadow-lg hover:shadow-xl transition-all duration-300 transform hover:scale-105">
        <i class="fas fa-plus-circle mr-2"></i> Faire une nouvelle demande de location
    </a>
</div>

<h3 class="dashboard-subheading text-2xl font-semibold text-gray-700 mt-8 mb-4 text-center">Actions Rapides</h3>
<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
    <a href="#" onclick="showTab('voituresDisponibles'); return false;" class="btn-primary quick-link bg-blue-500 hover:bg-blue-600">
        <i class="fas fa-search"></i> Chercher une voiture
    </a>
    <a href="#" onclick="showTab('mesLocations'); return false;" class="btn-primary quick-link bg-green-500 hover:bg-green-600">
        <i class="fas fa-history"></i> Voir l'historique de mes locations
    </a>
</div>

<%-- Section pour les locations récentes du client --%>
<div class="mt-8">
    <h3 class="text-2xl font-semibold text-gray-700 mb-4">Mes Demandes Récentes</h3>
    <c:if test="${not empty requestScope.recentClientRentals}">
        <div class="overflow-x-auto rounded-lg shadow">
            <table>
                <thead>
                    <tr>
                        <th>Location ID</th>
                        <th>Voiture</th>
                        <th>Date Début</th>
                        <th>Jours</th>
                        <th>Statut</th>
                        <th>Montant</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="rental" items="${requestScope.recentClientRentals}">
                        <tr>
                            <td class="whitespace-nowrap">${rental.id}</td>
                            <td>${rental.voiture.marque} (${rental.voiture.immatriculation})</td>
                            <td class="whitespace-nowrap"><fmt:formatDate value="${rental.legacyDateDebut}" pattern="dd/MM/yyyy" /></td>
                            <td>${rental.nombreJours}</td>
                            <td>
                                <span class="badge-status
                                    <c:choose>
                                        <c:when test="${rental.statut eq 'En attente'}">yellow</c:when>
                                        <c:when test="${rental.statut eq 'En cours'}">blue</c:when>
                                        <c:when test="${rental.statut eq 'Terminee'}">green-status</c:when>
                                        <c:when test="${rental.statut eq 'Annulee'}">red-status</c:when>
                                        <c:otherwise>gray-status</c:otherwise>
                                    </c:choose>
                                ">
                                    <c:out value="${rental.statut}"/>
                                </span>
                            </td>
                            <td class="whitespace-nowrap"><fmt:formatNumber value="${rental.montantTotal}" pattern="#,##0.00" /> €</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>
    <c:if test="${empty requestScope.recentClientRentals}">
        <p class="text-gray-600 py-4 text-center">Aucune demande de location récente.</p>
    </c:if>
</div>