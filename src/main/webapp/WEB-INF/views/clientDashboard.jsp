<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mon Tableau de Bord Client - Agence de Location</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="dashboard-container">
        <h2 class="dashboard-heading">Bonjour <c:out value="${sessionScope.client.prenom}"/> !</h2>
        <p class="text-lg text-gray-700 text-center mb-8">Bienvenue sur votre espace personnel. Gérez vos locations et trouvez votre prochaine voiture.</p>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
            <!-- Carte: Mes Locations -->
            <div class="dashboard-card cursor-pointer transition-transform hover:translate-y-[-3px] hover:shadow-lg" onclick="location.href='<c:url value="/clientRental?action=listMyRentals"/>'">
                <div>
                    <h3 class="dashboard-card-title">Mes Locations</h3>
                    <p class="dashboard-card-value text-blue-600">
                        <c:out value="${requestScope.clientRentalsCount != null ? requestScope.clientRentalsCount : 'N/A'}"/>
                    </p>
                    <span class="text-sm text-gray-500">Demandes actives et passées</span>
                </div>
                <div class="dashboard-card-icon text-blue-500">
                    <i class="fas fa-clipboard-list"></i>
                </div>
            </div>

            <!-- Carte: Nos Voitures Disponibles -->
            <div class="dashboard-card cursor-pointer transition-transform hover:translate-y-[-3px] hover:shadow-lg" onclick="location.href='<c:url value="/clientVoitures?action=listAvailable"/>'">
                <div>
                    <h3 class="dashboard-card-title">Nos Voitures</h3>
                    <p class="dashboard-card-value text-green-600">
                        <c:out value="${requestScope.availableCarsClientCount != null ? requestScope.availableCarsClientCount : 'N/A'}"/>
                    </p>
                    <span class="text-sm text-gray-500">Actuellement disponibles à la location</span>
                </div>
                <div class="dashboard-card-icon text-green-500">
                    <i class="fas fa-car"></i>
                </div>
            </div>
        </div>

        <h3 class="dashboard-subheading">Actions Rapides</h3>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <a href="<c:url value="/clientVoitures?action=listAvailable"/>" class="quick-link bg-blue-500 hover:bg-blue-600">
                <i class="fas fa-search"></i> Chercher une voiture
            </a>
            <a href="<c:url value="/clientRental?action=listMyRentals"/>" class="quick-link bg-green-500 hover:bg-green-600">
                <i class="fas fa-history"></i> Voir l'historique de mes locations
            </a>
        </div>
        
        <%-- Vous pourriez ajouter ici une section pour les locations récentes du client, si disponible --%>
        <div class="dashboard-container mt-8">
            <h3 class="text-xl font-semibold text-gray-800 mb-4">Mes Demandes Récentes</h3>
            <c:if test="${not empty requestScope.recentClientRentals}">
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
                                <td>${rental.id}</td>
                                <td>${rental.voiture.marque} (${rental.voiture.immatriculation})</td>
                                <td><fmt:formatDate value="${rental.legacyDateDebut}" pattern="dd/MM/yyyy" /></td>
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
                                <td><fmt:formatNumber value="${rental.montantTotal}" pattern="#,##0.00" /> €</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
            <c:if test="${empty requestScope.recentClientRentals}">
                <p class="text-gray-600">Aucune demande de location récente.</p>
            </c:if>
        </div>
    </div>
</body>
</html>
