<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


    <div class="content-area"> <%-- Conteneur principal pour le padding et la largeur max --%>
        <main class="main-content-card"> <%-- Arrière-plan et ombre pour le contenu principal --%>
            <h2 class="dashboard-heading text-3xl font-bold text-gray-800 mb-4 text-center">Mes Locations</h2>
            <p class="text-lg text-gray-600 text-center mb-8">Retrouvez ici l'historique de toutes vos locations passées et actuelles.</p>

            <%-- Messages de succès ou d'erreur --%>
            <c:if test="${not empty requestScope.message}">
                <p class="success-message">${requestScope.message}</p>
            </c:if>
            <c:if test="${not empty requestScope.error}">
                <p class="error-message">${requestScope.error}</p>
            </c:if>

            <%-- Vérification si le client est connecté pour afficher les locations --%>
            <c:choose>
                <c:when test="${sessionScope.client != null}">
                    <div class="rental-table-container">
                        <c:choose>
                            <c:when test="${not empty requestScope.clientLocations}">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>Location ID</th>
                                            <th>Voiture</th>
                                            <th>Date Début</th>
                                            <th>Date Retour Prévue</th>
                                            <th>Jours</th>
                                            <th>Montant Total</th>
                                            <th>Statut</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="rental" items="${requestScope.clientLocations}">
                                            <tr>
                                                <td><c:out value="${rental.id}"/></td>
                                                <td><c:out value="${rental.voiture.marque}"/> (<c:out value="${rental.voiture.immatriculation}"/>)</td>
                                                <td><fmt:formatDate value="${rental.legacyDateDebut}" pattern="dd/MM/yyyy" /></td>
                                                <td><fmt:formatDate value="${rental.legacyDateRetourPrevue}" pattern="dd/MM/yyyy" /></td>
                                                <td><c:out value="${rental.nombreJours}"/></td>
                                                <td><fmt:formatNumber value="${rental.montantTotal}" pattern="#,##0.00" /> €</td>
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
                                                <td class="action-buttons-group">
                                                    <c:if test="${rental.statut eq 'En attente'}">
                                                        <a href="<c:url value="/clientRental?action=cancelRequest&id=${rental.id}"/>" class="cancel-btn"
                                                           onclick="return confirm('Êtes-vous sûr de vouloir annuler cette demande de location ?');">
                                                            <i class="fas fa-times-circle"></i> Annuler
                                                        </a>
                                                    </c:if>
                                                    <c:if test="${rental.statut eq 'En cours' || rental.statut eq 'Terminee'}">
                                                        <a href="<c:url value="/reports?action=generateInvoice&locationId=${rental.id}"/>" class="bg-gray-700 hover:bg-gray-800" target="_blank">
                                                            <i class="fas fa-file-pdf"></i> Facture
                                                        </a>
                                                    </c:if>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <p class="text-gray-600 py-4 text-center">Aucune demande de location trouvée.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="error-message text-center py-4">Veuillez vous connecter pour voir vos locations.</p>
                </c:otherwise>
            </c:choose>
        </main>
    </div>

