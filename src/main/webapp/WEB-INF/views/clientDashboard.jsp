<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tableau de Bord Client - Agence de Location</title>
    <link rel="stylesheet" href="css/client-style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <script src="js/client-dashboard.js" defer></script> <%-- Assurez-vous que ce fichier existe --%>
</head>
<body class="has-dashboard">
    <%-- Inclusion de la barre de navigation --%>
    <jsp:include page="navbar.jsp"/>

    <div class="main-content dashboard-container">
        <h1 class="text-4xl font-extrabold text-gray-900 mb-8 text-center">
            Bienvenue, <c:out value="${sessionScope.client.prenom}"/> !
        </h1>

        <%-- Message d'erreur ou de succès global --%>
        <c:if test="${not empty requestScope.error}">
            <div class="error-message text-center mb-4 p-3 rounded-lg bg-red-100 text-red-700 border border-red-200">
                <c:out value="${requestScope.error}"/>
            </div>
        </c:if>
        <c:if test="${not empty requestScope.message}">
            <div class="success-message text-center mb-4 p-3 rounded-lg bg-green-100 text-green-700 border border-green-200">
                <c:out value="${requestScope.message}"/>
            </div>
        </c:if>

        <div class="tabs-container mb-6">
            <nav class="tabs-nav">
                <a href="#" class="tab-item active" data-tab="overview">
                    <i class="fas fa-tachometer-alt mr-2"></i> Aperçu
                </a>
                <a href="#" class="tab-item" data-tab="cars">
                    <i class="fas fa-car mr-2"></i> Voitures
                </a>
                <a href="#" class="tab-item" data-tab="rentals">
                    <i class="fas fa-clipboard-list mr-2"></i> Mes Locations
                </a>
                <a href="#" class="tab-item" data-tab="profile">
                    <i class="fas fa-user-circle mr-2"></i> Mon Profil
                </a>
            </nav>
        </div>

        <div id="tab-content-overview" class="tab-content active">
            <%-- Chemin corrigé si les JSPs sont directement sous WEB-INF/views/ --%>
            <jsp:include page="/WEB-INF/views/clientDashboardOverview.jsp"/>
        </div>

        <div id="tab-content-cars" class="tab-content hidden">
            <%-- Chemin corrigé --%>
            <jsp:include page="/WEB-INF/views/clientVoituresList.jsp"/>
        </div>

        <div id="tab-content-rentals" class="tab-content hidden">
            <%-- Chemin corrigé --%>
            <jsp:include page="/WEB-INF/views/clientLocationList.jsp"/>
        </div>

        <div id="tab-content-profile" class="tab-content hidden">
            <%-- Chemin corrigé --%>
            <jsp:include page="/WEB-INF/views/clientProfile.jsp"/>
        </div>
    </div>
</body>
</html>
