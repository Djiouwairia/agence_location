<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes Locations - Agence de Location</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="content-area">
        <main class="main-content-card">
            <h2 class="dashboard-heading text-3xl font-bold text-gray-800 mb-4 text-center">Mes Locations</h2>
            <p class="text-lg text-gray-600 text-center mb-8">Retrouvez ici toutes vos demandes et locations passées et actuelles.</p>

            <%-- Messages de succès ou d'erreur --%>
            <c:if test="${not empty sessionScope.message}">
                <p class="success-message">${sessionScope.message}</p>
                <c:remove var="message" scope="session"/>
            </c:if>
            <c:if test="${not empty requestScope.error}">
                <p class="error-message">${requestScope.error}</p>
            </c:if>
            <c:if test="${not empty sessionScope.error}">
                <p class="error-message">${sessionScope.error}</p>
                <c:remove var="error" scope="session"/>
            </c:if>

            <c:choose>
                <c:when test="${not empty requestScope.myLocations}">
                    <div class="overflow-x-auto rounded-lg shadow">
                        <table class="min-w-full divide-y divide-gray-200">
                            <thead class="bg-gray-50">
                                <tr>
                                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID Location</th>
                                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Voiture (Immat.)</th>
                                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date Début</th>
                                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date Retour Prévue</th>
                                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date Retour Réelle</th>
                                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Statut</th>
                                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Montant Total</th>
                                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                                </tr>
                            </thead>
                            <tbody class="bg-white divide-y divide-gray-200">
                                <c:forEach var="location" items="${requestScope.myLocations}">
                                    <tr>
                                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${location.id}</td>
                                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${location.voiture.marque} (${location.voiture.immatriculation})</td>
                                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500"><fmt:formatDate value="${location.dateDebut}" pattern="dd/MM/yyyy" /></td>
                                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500"><fmt:formatDate value="${location.dateRetourPrevue}" pattern="dd/MM/yyyy" /></td>
                                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                            <c:choose>
                                                <c:when test="${location.dateRetourReelle != null}">
                                                    <fmt:formatDate value="${location.dateRetourReelle}" pattern="dd/MM/yyyy" />
                                                </c:when>
                                                <c:otherwise>
                                                    N/A
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap text-sm">
                                            <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full
                                                <c:choose>
                                                    <c:when test="${location.statut eq 'En attente'}">bg-yellow-100 text-yellow-800</c:when>
                                                    <c:when test="${location.statut eq 'En cours'}">bg-blue-100 text-blue-800</c:when>
                                                    <c:when test="${location.statut eq 'Terminee'}">bg-green-100 text-green-800</c:when>
                                                    <c:when test="${location.statut eq 'Annulee'}">bg-red-100 text-red-800</c:when>
                                                    <c:otherwise>bg-gray-100 text-gray-800</c:otherwise>
                                                </c:choose>
                                            ">
                                                <c:out value="${location.statut}"/>
                                            </span>
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                            <c:choose>
                                                <c:when test="${location.montantTotal != null}">
                                                    <fmt:formatNumber value="${location.montantTotal}" pattern="#,##0.00" /> €
                                                </c:when>
                                                <c:otherwise>
                                                    (À calculer)
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                            <c:if test="${location.statut eq 'En attente'}">
                                                <a href="${pageContext.request.contextPath}/clientRental?action=cancelRequest&id=${location.id}" 
                                                   onclick="return confirm('Êtes-vous sûr de vouloir annuler cette demande de location ?');" 
                                                   class="text-red-600 hover:text-red-900">
                                                    <i class="fas fa-times-circle mr-1"></i> Annuler
                                                </a>
                                            </c:if>
                                            <%-- Ajoutez d'autres actions si nécessaire (ex: voir détails) --%>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="text-gray-600 py-4 text-center">Vous n'avez aucune location enregistrée pour le moment.</p>
                </c:otherwise>
            </c:choose>
        </main>
    </div>
</body>
</html>
