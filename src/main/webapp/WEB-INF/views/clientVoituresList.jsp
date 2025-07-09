<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%--
    Ce fichier JSP est un FRAGMENT de contenu pour l'onglet "Voitures Disponibles".
    Il est inclus dynamiquement dans clientDashboard.jsp.
    Il ne doit PAS contenir de balises <html>, <head>, <body>, <title> ou de liens <link> CSS.
    Ces éléments sont gérés par clientDashboard.jsp.
--%>

    <%-- Messages de succès ou d'erreur --%>
    <c:if test="${not empty requestScope.message}">
        <p class="success-message">${requestScope.message}</p>
    </c:if>
    <c:if test="${not empty requestScope.error}">
        <p class="error-message">${requestScope.error}</p>
    </c:if>

    <%-- Tableau des voitures disponibles --%>
    <div class="car-table-container">
        <c:choose>
            <c:when test="${not empty requestScope.voituresDisponibles}">
                <table>
                    <thead>
                        <tr>
                            <th>Immatriculation</th>
                            <th>Marque</th>
                            <th>Modèle</th>
                            <th>Catégorie</th>
                            <th>Prix/Jour</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="voiture" items="${requestScope.voituresDisponibles}">
                            <tr>
                                <td><c:out value="${voiture.immatriculation}"/></td>
                                <td><c:out value="${voiture.marque}"/></td>
                                <td><c:out value="${voiture.modele}"/></td>
                                <td><c:out value="${voiture.categorie}"/></td>
                                <td><fmt:formatNumber value="${voiture.prixLocationJ}" pattern="#,##0.00" /> €</td>
                                <td class="action-buttons-group">
                                    <a href="<c:url value="/clientVoitures?action=viewDetails&immatriculation=${voiture.immatriculation}"/>" class="view-details-btn">
                                        <i class="fas fa-info-circle"></i> Détails & Louer
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p class="text-gray-600 py-4 text-center">Aucune voiture disponible.</p>
            </c:otherwise>
        </c:choose>
    </div>
