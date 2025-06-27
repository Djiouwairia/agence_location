<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<nav class="bg-gray-800 text-white shadow-lg">
    <div class="navbar-container max-w-7xl mx-auto flex justify-between items-center">
        <!-- Logo de l'agence -->
        <div class="flex items-center">
            <a href="<c:url value="/"/>" class="navbar-brand">
                <i class="fas fa-car-alt"></i> Agence Loc.
            </a>
        </div>

        <!-- Liens de navigation principaux -->
        <div class="navbar-links hidden md:flex items-center space-x-6">
            <ul class="flex items-center space-x-6">
                <!-- Liens pour le PERSONNEL (Gestionnaire/ChefAgence) -->
                <c:if test="${not empty sessionScope.utilisateur}">
                    <c:choose>
                        <c:when test="${sessionScope.role eq 'Gestionnaire' }">
                            <%-- Correction : Ajout du texte "Tableau de bord" et suppression de la classe nav-link --%>
                            <li><a href="<c:url value="/dashboard"/>">Tableau de bord</a></li>
                            <li><a href="<c:url value="/clients?action=list"/>">Clients</a></li>
                            <li><a href="<c:url value="/voitures?action=list"/>">Voitures</a></li>
                            <li><a href="<c:url value="/locations?action=list"/>">Locations</a></li>
                          
                        </c:when>
                    </c:choose>
                </c:if>
                
                
                
                
                <!-- Liens pour le PERSONNEL (ChefAgence) -->                
                <c:if test="${not empty sessionScope.utilisateur}">
                    <c:choose>
                        <c:when test="${sessionScope.role eq 'ChefAgence'}">
                            <%-- Suppression de la classe nav-link --%>
                            <li><a href="<c:url value="/dashboard"/>">Tableau de bord</a></li>
                            <c:if test="${sessionScope.role eq 'ChefAgence'}">
                                <%-- Suppression de la classe nav-link --%>
                                <li><a href="<c:url value="/reports"/>">Rapports</a></li>
                            </c:if>
                        </c:when>
                    </c:choose>
                </c:if>

                <!-- Liens pour le CLIENT -->
                <c:if test="${not empty sessionScope.client}">
                    <c:choose>
                        <c:when test="${sessionScope.role eq 'Client'}">
                            <%-- Suppression de la classe nav-link --%>
                            <li><a href="<c:url value="/clientVoitures?action=listAvailable"/>">Voitures Disponibles</a></li>
                            <li><a href="<c:url value="/clientRental?action=listMyRentals"/>">Mes Locations</a></li>
                        </c:when>
                    </c:choose>
                </c:if>
            </ul>
        </div>

        <!-- Informations utilisateur et bouton de déconnexion -->
        <div class="flex items-center space-x-4">
            <c:if test="${not empty sessionScope.utilisateur or not empty sessionScope.client}">
                <span class="text-sm font-medium text-gray-300">
                    <c:choose>
                        <c:when test="${not empty sessionScope.utilisateur}">
                            ${sessionScope.utilisateur.prenom} (${sessionScope.role})
                        </c:when>
                        <c:when test="${not empty sessionScope.client}">
                            ${sessionScope.client.prenom} (${sessionScope.role})
                        </c:when>
                    </c:choose>
                </span>
                <%-- LIENS DE DÉCONNEXION --%>
                <c:choose>
                    <c:when test="${not empty sessionScope.utilisateur}">
                        <a href="${pageContext.request.contextPath}/auth?action=logout" class="bg-blue-600 text-white py-2 px-4 rounded-full text-sm font-semibold hover:bg-blue-700 transition-colors">
                            <i class="fas fa-sign-out-alt"></i> Déconnexion
                        </a>
                    </c:when>
                    <c:when test="${not empty sessionScope.client}">
                        <a href="${pageContext.request.contextPath}/clientAuth?action=logout" class="bg-blue-600 text-white py-2 px-4 rounded-full text-sm font-semibold hover:bg-blue-700 transition-colors">
                            <i class="fas fa-sign-out-alt"></i> Déconnexion
                        </a>
                    </c:when>
                </c:choose>
            </c:if>

            <c:if test="${empty sessionScope.utilisateur and empty sessionScope.client}">
                <%-- LIEN DE CONNEXION (si non connecté) --%>
                <a href="${pageContext.request.contextPath}/login.jsp" class="bg-blue-600 text-white py-2 px-4 rounded-full text-sm font-semibold hover:bg-blue-700 transition-colors">
                    <i class="fas fa-sign-in-alt"></i> Connexion
                </a>
            </c:if>
        </div>
    </div>
</nav>
