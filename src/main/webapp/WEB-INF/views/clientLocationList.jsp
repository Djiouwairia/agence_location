<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<div class="main-content-card">
    <h2 class="text-2xl font-bold text-gray-800 mb-4">Toutes mes Locations</h2>

    <c:if test="${empty clientLocations}">
        <p class="text-gray-600">Vous n'avez pas encore effectué de locations.</p>
    </c:if>
    <c:if test="${not empty clientLocations}">
        <div class="overflow-x-auto">
            <table class="data-table w-full text-left">
                <thead>
                    <tr class="bg-gray-100 text-gray-600 uppercase text-sm leading-normal">
                        <th class="py-3 px-6">ID Location</th>
                        <th class="py-3 px-6">Voiture</th>
                        <th class="py-3 px-6">Date Début</th>
                        <th class="py-3 px-6">Date Retour Prévue</th>
                        <th class="py-3 px-6">Date Retour Réelle</th>
                        <th class="py-3 px-6">Montant Total</th>
                        <th class="py-3 px-6">Statut</th>
                        <th class="py-3 px-6">Actions</th>
                    </tr>
                </thead>
                <tbody class="text-gray-700 text-sm font-light">
                    <c:forEach var="location" items="${clientLocations}">
                        <tr class="border-b border-gray-200 hover:bg-gray-100">
                            <td class="py-3 px-6"><c:out value="${location.id}"/></td>
                            <td class="py-3 px-6 whitespace-nowrap">
                                <%-- Vérification si l'objet voiture est null --%>
                                <c:choose>
                                    <c:when test="${location.voiture != null}">
                                        <c:out value="${location.voiture.marque}"/> <c:out value="${location.voiture.modele}"/> (<c:out value="${location.voiture.immatriculation}"/>)
                                    </c:when>
                                    <c:otherwise>
                                        Voiture non disponible
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="py-3 px-6 whitespace-nowrap">
                                <fmt:formatDate value="${location.legacyDateDebut}" pattern="dd/MM/yyyy"/>
                            </td>
                            <td class="py-3 px-6 whitespace-nowrap">
                                <fmt:formatDate value="${location.legacyDateRetourPrevue}" pattern="dd/MM/yyyy"/>
                            </td>
                            <td class="py-3 px-6 whitespace-nowrap">
                                <%-- Vérification si la date de retour réelle est null --%>
                                <c:choose>
                                    <c:when test="${location.dateRetourReelle != null}">
                                        <fmt:formatDate value="${location.legacyDateRetourReelle}" pattern="dd/MM/yyyy"/>
                                    </c:when>
                                    <c:otherwise>
                                        En cours
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="py-3 px-6">
                                <%-- Vérification si le montant total est null --%>
                                <c:choose>
                                    <c:when test="${location.montantTotal != null}">
                                        <fmt:formatNumber value="${location.montantTotal}" type="currency" currencySymbol="€" maxFractionDigits="2"/>
                                    </c:when>
                                    <c:otherwise>
                                        N/A
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="py-3 px-6">
                                <span class="badge-status
                                    <c:choose>
                                        <c:when test="${location.statut eq 'En attente'}">yellow-status</c:when>
                                        <c:when test="${location.statut eq 'En cours'}">blue-status</c:when>
                                        <c:when test="${location.statut eq 'Terminee'}">green-status</c:when>
                                        <c:when test="${location.statut eq 'Annulee'}">red-status</c:when>
                                        <c:otherwise>gray-status</c:otherwise>
                                    </c:choose>
                                ">
                                    <c:out value="${location.statut}"/>
                                </span>
                            </td>
                            <td class="py-3 px-6 action-buttons-group">
                                <c:if test="${location.statut eq 'En attente'}">
                                    <a href="<c:url value="/clientRental?action=cancelRequest&id=${location.id}"/>" class="cancel-btn"
                                       onclick="return confirm('Êtes-vous sûr de vouloir annuler cette demande de location ?');">
                                        <i class="fas fa-times-circle"></i> Annuler
                                    </a>
                                </c:if>
                                <c:if test="${location.statut eq 'Confirmée' || location.statut eq 'En cours' || location.statut eq 'Terminee'}">
                                    <a href="<c:url value="/reports?action=generateInvoice&locationId=${location.id}"/>" class="btn-secondary" target="_blank">
                                        <i class="fas fa-file-pdf"></i> Facture
                                    </a>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>
</div>
