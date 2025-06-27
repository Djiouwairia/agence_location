<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tableau de Bord Chef d'Agence</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="dashboard-container">
        <h2 class="dashboard-heading">Tableau de Bord du Chef d'Agence</h2>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
            <!-- Carte 1: Total Voitures -->
            <div class="dashboard-card">
                <div>
                    <h3 class="dashboard-card-title">Total Voitures</h3>
                    <p class="dashboard-card-value text-gray-800">
                        <c:out value="${requestScope.nombreTotalVoitures != null ? requestScope.nombreTotalVoitures : 'N/A'}"/>
                    </p>
                </div>
                <div class="dashboard-card-icon text-gray-500">
                    <i class="fas fa-car-alt"></i>
                </div>
            </div>

            <!-- Carte 2: Voitures Disponibles -->
            <div class="dashboard-card">
                <div>
                    <h3 class="dashboard-card-title">Voitures Disponibles</h3>
                    <p class="dashboard-card-value text-green-600">
                        <c:out value="${requestScope.nombreVoituresDisponibles != null ? requestScope.nombreVoituresDisponibles : 'N/A'}"/>
                    </p>
                </div>
                <div class="dashboard-card-icon text-green-500">
                    <i class="fas fa-car"></i>
                </div>
            </div>

            <!-- Carte 3: Voitures Louées -->
            <div class="dashboard-card">
                <div>
                    <h3 class="dashboard-card-title">Voitures Louées</h3>
                    <p class="dashboard-card-value text-yellow-600">
                        <c:out value="${requestScope.nombreVoituresLouees != null ? requestScope.nombreVoituresLouees : 'N/A'}"/>
                    </p>
                </div>
                <div class="dashboard-card-icon text-yellow-500">
                    <i class="fas fa-key"></i>
                </div>
            </div>

            <!-- Carte 4: Demandes en Attente -->
            <div class="dashboard-card">
                <div>
                    <h3 class="dashboard-card-title">Demandes en Attente</h3>
                    <p class="dashboard-card-value text-red-600">
                        <c:out value="${requestScope.pendingRequestsCount != null ? requestScope.pendingRequestsCount : 'N/A'}"/>
                    </p>
                </div>
                <div class="dashboard-card-icon text-red-500">
                    <i class="fas fa-hourglass-half"></i>
                </div>
            </div>
        </div>

        <h3 class="dashboard-subheading">Accès Rapide</h3>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-8">
            <a href="<c:url value="/locations?action=list"/>" class="quick-link bg-blue-500 hover:bg-blue-600">
                <i class="fas fa-clipboard-list"></i> Gérer les Locations
            </a>
            <a href="<c:url value="/clients"/>" class="quick-link bg-green-500 hover:bg-green-600">
                <i class="fas fa-users-cog"></i> Gérer les Clients
            </a>
            <a href="<c:url value="/voitures"/>" class="quick-link bg-purple-500 hover:bg-purple-600">
                <i class="fas fa-car-side"></i> Gérer les Voitures
            </a>
            <a href="<c:url value="/locations?action=new"/>" class="quick-link bg-yellow-500 text-gray-800 hover:bg-yellow-600">
                <i class="fas fa-plus-circle"></i> Enregistrer Nouvelle Location
            </a>
            <a href="<c:url value="/reports"/>" class="quick-link bg-indigo-600 text-white hover:bg-indigo-700">
                <i class="fas fa-chart-line"></i> Accéder aux Rapports
            </a>
        </div>

        <%-- Section: Voitures Louées Actuellement avec infos locataires --%>
        <div class="dashboard-container mt-8">
            <h3 class="text-xl font-semibold text-gray-800 mb-4">Voitures Actuellement Louées</h3>
            <c:if test="${not empty requestScope.voituresLoueesAvecInfosLocataires}">
                <table>
                    <thead>
                        <tr>
                            <th>Immatriculation</th>
                            <th>Modèle</th>
                            <th>Marque</th>
                            <th>Locataire (CIN)</th>
                            <th>Date Début</th>
                            <th>Jours</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="location" items="${requestScope.voituresLoueesAvecInfosLocataires}">
                            <tr>
                                <td><c:out value="${location.voiture.immatriculation}"/></td>
                                <td><c:out value="${location.voiture.modele}"/></td>
                                <td><c:out value="${location.voiture.marque}"/></td>
                                <td><c:out value="${location.client.nom}"/> (<c:out value="${location.client.cin}"/>)</td>
                                <td><fmt:formatDate value="${location.legacyDateDebut}" pattern="dd/MM/yyyy" /></td>
                                <td><c:out value="${location.nombreJours}"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
            <c:if test="${empty requestScope.voituresLoueesAvecInfosLocataires}">
                <p class="text-gray-600">Aucune voiture n'est actuellement louée.</p>
            </c:if>
        </div>

        <%-- Section: Voitures les Plus Recherchées (Top 5) --%>
        <div class="dashboard-container mt-8">
            <h3 class="text-xl font-semibold text-gray-800 mb-4">Top 5 des Voitures les Plus Recherchées</h3>
            <c:if test="${not empty requestScope.voituresPlusRecherches}">
                <table>
                    <thead>
                        <tr>
                            <th>Marque</th>
                            <th>Modèle</th>
                            <th>Immatriculation</th>
                            <th>Prix/Jour (€)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="voiture" items="${requestScope.voituresPlusRecherches}">
                            <tr>
                                <td><c:out value="${voiture.marque}"/></td>
                                <td><c:out value="${voiture.modele}"/></td>
                                <td><c:out value="${voiture.immatriculation}"/></td>
                                <td><fmt:formatNumber value="${voiture.prixLocationJ}" pattern="#,##0.00" /></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
            <c:if test="${empty requestScope.voituresPlusRecherches}">
                <p class="text-gray-600">Aucune donnée sur les voitures les plus recherchées pour le moment.</p>
            </c:if>
        </div>

        <%-- Section: Bilan Financier Mensuel --%>
        <div class="dashboard-container mt-8">
            <h3 class="text-xl font-semibold text-gray-800 mb-4">Bilan Financier Mensuel (<c:out value="${requestScope.moisBilan}"/> <c:out value="${requestScope.anneeBilan}"/>)</h3>
            <c:if test="${not empty requestScope.bilanMensuel}">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div class="dashboard-card bg-blue-50">
                        <div>
                            <h4 class="dashboard-card-title text-blue-700">Revenu Total</h4>
                            <p class="dashboard-card-value text-blue-600">
                                <fmt:formatNumber value="${requestScope.bilanMensuel.totalRevenue}" pattern="#,##0.00" /> €
                            </p>
                        </div>
                        <div class="dashboard-card-icon text-blue-500">
                            <i class="fas fa-euro-sign"></i>
                        </div>
                    </div>
                    <div class="dashboard-card bg-green-50">
                        <div>
                            <h4 class="dashboard-card-title text-green-700">Total Locations</h4>
                            <p class="dashboard-card-value text-green-600">
                                <c:out value="${requestScope.bilanMensuel.totalRentals}"/>
                            </p>
                        </div>
                        <div class="dashboard-card-icon text-green-500">
                            <i class="fas fa-receipt"></i>
                        </div>
                    </div>
                </div>
            </c:if>
            <c:if test="${empty requestScope.bilanMensuel}">
                <p class="text-gray-600">Aucun bilan financier pour le mois sélectionné.</p>
            </c:if>
        </div>

    </div>
</body>
</html>
