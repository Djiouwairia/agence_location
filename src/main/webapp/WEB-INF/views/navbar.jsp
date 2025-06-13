<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- Inclure cette page dans les JSPs qui nécessitent la navigation --%>

<nav class="navbar">
    <div class="navbar-container">
        <a href="<c:url value="/dashboard"/>" class="navbar-brand">Agence Loc.</a>
        <div class="navbar-links">
            <ul>
                <c:if test="${sessionScope.role == 'Gestionnaire' || sessionScope.role == 'ChefAgence'}">
                    <li><a href="<c:url value="/dashboard"/>">Tableau de bord</a></li>
                </c:if>
                <c:if test="${sessionScope.role == 'Gestionnaire'}">
                    <li><a href="<c:url value="/clients"/>">Clients</a></li>
                    <li><a href="<c:url value="/voitures"/>">Voitures</a></li>
                    <li><a href="<c:url value="/locations"/>">Locations</a></li>
                </c:if>
                <li><a href="<c:url value="/auth?action=logout"/>">Déconnexion</a></li>
            </ul>
        </div>
    </div>
</nav>
