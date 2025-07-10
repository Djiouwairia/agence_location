<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- Ce fichier contient la barre de navigation --%>

<nav class="bg-gray-800 text-white shadow-lg py-3 px-4">
    <div class="navbar-container max-w-7xl mx-auto flex justify-between items-center">
        <!-- Logo de l'agence -->
        <div class="flex items-center">
            <a href="<c:url value="/"/>" class="navbar-brand text-2xl font-bold flex items-center space-x-2 rounded-md p-2 hover:bg-gray-700 transition-colors">
                <i class="fas fa-car-alt text-blue-400"></i> <span class="text-white">Agence Loc.</span>
            </a>
        </div>

        <!-- Liens de navigation principaux -->
        <div class="navbar-links hidden md:flex items-center space-x-6">
            <ul class="flex items-center space-x-6">
                <!-- Liens pour le PERSONNEL (Gestionnaire/ChefAgence) -->
                <c:if test="${not empty sessionScope.utilisateur && (sessionScope.role eq 'Gestionnaire' || sessionScope.role eq 'ChefAgence')}">
                    <li><a href="<c:url value="/dashboard"/>" class="nav-link text-gray-300 hover:text-white transition-colors">Tableau de bord</a></li>
                    <c:if test="${sessionScope.role eq 'Gestionnaire'}">
                        <li><a href="<c:url value="/clients?action=list"/>" class="nav-link text-gray-300 hover:text-white transition-colors">Clients</a></li>
                        <li><a href="<c:url value="/voitures?action=list"/>" class="nav-link text-gray-300 hover:text-white transition-colors">Voitures</a></li>
                        <li><a href="<c:url value="/locations?action=list"/>" class="nav-link text-gray-300 hover:text-white transition-colors">Locations</a></li>
                    </c:if>
                    <%-- Les liens spécifiques à ChefAgence peuvent être ajoutés ici si différents de Gestionnaire --%>
                </c:if>
                
                <!-- Liens pour le CLIENT -->
                <c:if test="${not empty sessionScope.utilisateur && sessionScope.role eq 'Client'}">
                    <li><a href="<c:url value="/clientDashboard"/>" class="nav-link text-gray-300 hover:text-white transition-colors">Vue d'ensemble</a></li>
                    <li><a href="<c:url value="/clientVoitures?action=listAvailable"/>" class="nav-link text-gray-300 hover:text-white transition-colors">Voitures Disponibles</a></li>
                    <li><a href="<c:url value="/clientRental?action=listMyRentals"/>" class="nav-link text-gray-300 hover:text-white transition-colors">Mes Locations</a></li>
                    <li><a href="<c:url value="/clientProfile"/>" class="nav-link text-gray-300 hover:text-white transition-colors">Mon Profil</a></li>
                </c:if>
            </ul>
        </div>

        <!-- Informations utilisateur et bouton de déconnexion -->
        <div class="flex items-center space-x-4">
            <c:if test="${not empty sessionScope.utilisateur}">
                <span class="text-sm font-medium text-gray-300">
                    Bonjour, ${sessionScope.utilisateur.prenom} (${sessionScope.role})
                </span>
                <%-- LIEN DE DÉCONNEXION --%>
                <a href="${pageContext.request.contextPath}/auth?action=logout" class="bg-blue-600 text-white py-2 px-4 rounded-full text-sm font-semibold hover:bg-blue-700 transition-colors shadow-md">
                    <i class="fas fa-sign-out-alt mr-1"></i> Déconnexion
                </a>
            </c:if>

            <c:if test="${empty sessionScope.utilisateur}">
                <%-- LIEN DE CONNEXION (si non connecté) --%>
                <a href="${pageContext.request.contextPath}/login.jsp" class="bg-blue-600 text-white py-2 px-4 rounded-full text-sm font-semibold hover:bg-blue-700 transition-colors shadow-md">
                    <i class="fas fa-sign-in-alt mr-1"></i> Deconnexion
                </a>
            </c:if>
        </div>
    </div>
</nav>
