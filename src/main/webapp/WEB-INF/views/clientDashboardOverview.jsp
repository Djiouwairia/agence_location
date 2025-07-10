<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
    <div class="card bg-white p-6 rounded-xl shadow-md text-center">
        <h3 class="text-xl font-semibold text-gray-700 mb-2">Locations en cours/passées</h3>
        <p class="card-value text-indigo-600">
            <c:out value="${clientRentalsCount != null ? clientRentalsCount : 0}"/>
        </p>
    </div>
    <div class="card bg-white p-6 rounded-xl shadow-md text-center">
        <h3 class="text-xl font-semibold text-gray-700 mb-2">Voitures disponibles</h3>
        <p class="card-value text-green-600">
            <c:out value="${availableCarsClientCount != null ? availableCarsClientCount : 0}"/>
        </p>
    </div>
    <div class="card bg-white p-6 rounded-xl shadow-md text-center">
        <h3 class="text-xl font-semibold text-gray-700 mb-2">Demandes en attente</h3>
        <p class="card-value text-yellow-600">0</p> <%-- Ce champ n'est pas encore implémenté côté Java --%>
    </div>
</div>

<div class="main-content-card">
    <h2 class="text-2xl font-bold text-gray-800 mb-4">Mes Locations Récentes</h2>
    <c:if test="${empty recentClientRentals}">
        <p class="text-gray-600">Vous n'avez pas de locations récentes.</p>
    </c:if>
    <c:if test="${not empty recentClientRentals}">
        <div class="overflow-x-auto">
            <table class="data-table w-full text-left">
                <thead>
                    <tr class="bg-gray-100 text-gray-600 uppercase text-sm leading-normal">
                        <th class="py-3 px-6">Voiture</th>
                        <th class="py-3 px-6">Date Début</th>
                        <th class="py-3 px-6">Jours</th>
                        <th class="py-3 px-6">Montant Total</th>
                        <th class="py-3 px-6">Statut</th>
                    </tr>
                </thead>
                <tbody class="text-gray-700 text-sm font-light">
                    <c:forEach var="location" items="${recentClientRentals}">
                        <tr class="border-b border-gray-200 hover:bg-gray-100">
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
                            <td class="py-3 px-6"><c:out value="${location.nombreJours}"/></td>
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
                            <td class="py-3 px-6"><c:out value="${location.statut}"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:if>
</div>
