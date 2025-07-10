<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%--
    Ce fichier JSP est un FRAGMENT de contenu pour l'onglet "Voitures Disponibles" en vue carte.
    Il est inclus dynamiquement dans clientDashboard.jsp.
    Il ne doit PAS contenir de balises <html>, <head>, <body>, <title> ou de liens <link> CSS.
    Ces éléments sont gérés par clientDashboard.jsp.
--%>

<c:if test="${not empty requestScope.message}">
    <p class="success-message">${requestScope.message}</p>
</c:if>
<c:if test="${not empty requestScope.error}">
    <p class="error-message">${requestScope.error}</p>
</c:if>

<%-- Formulaire de recherche/filtrage --%>
<div class="filter-form-grid mb-8">
    <form action="clientVoitures" method="get" class="col-span-full grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
        <input type="hidden" name="action" value="listAvailable">
        <input type="hidden" name="tab" value="cars"> <%-- CORRECTION ICI: 'cars' pour correspondre au data-tab du bouton --%>

        <div class="form-group">
            <label for="searchMarque" class="form-label">Marque:</label>
            <input type="text" id="searchMarque" name="marque" class="form-input" placeholder="Ex: Renault" value="${param.marque}">
        </div>
        <div class="form-group">
            <label for="searchCategorie" class="form-label">Catégorie:</label>
            <input type="text" id="searchCategorie" name="categorie" class="form-input" placeholder="Ex: Berline" value="${param.categorie}">
        </div>
        <div class="form-group">
            <label for="searchPrixMax" class="form-label">Prix Max/Jour (€):</label>
            <input type="number" id="searchPrixMax" name="prixMax" class="form-input" placeholder="Ex: 50" value="${param.prixMax}">
        </div>
        <div class="form-group">
            <label for="searchKilometrageMax" class="form-label">Kilométrage Max (km):</label>
            <input type="number" id="searchKilometrageMax" name="kilometrageMax" class="form-input" placeholder="Ex: 100000" value="${param.kilometrageMax}">
        </div>
        <div class="form-group">
            <label for="searchNbPlaces" class="form-label">Nb Places:</label>
            <input type="number" id="searchNbPlaces" name="nbPlaces" class="form-input" min="1" placeholder="Ex: 5" value="${param.nbPlaces}">
        </div>
        <div class="form-group">
            <label for="searchTypeCarburant" class="form-label">Type Carburant:</label>
            <select id="searchTypeCarburant" name="typeCarburant" class="form-input">
                <option value="">Tous</option>
                <option value="Essence" <c:if test="${param.typeCarburant == 'Essence'}">selected</c:if>>Essence</option>
                <option value="Diesel" <c:if test="${param.typeCarburant == 'Diesel'}">selected</c:if>>Diesel</option>
                <option value="Électrique" <c:if test="${param.typeCarburant == 'Électrique'}">selected</c:if>>Électrique</option>
                <option value="Hybride" <c:if test="${param.typeCarburant == 'Hybride'}">selected</c:if>>Hybride</option>
            </select>
        </div>
        <button type="submit" class="btn-primary filter-button md:col-span-1">
            <i class="fas fa-filter mr-2"></i> Filtrer
        </button>
        <button type="button" onclick="window.location.href='clientVoitures?action=listAvailable&tab=cars'" class="btn-primary filter-button bg-gray-500 hover:bg-gray-600 md:col-span-1">
            <i class="fas fa-undo mr-2"></i> Réinitialiser
        </button>
    </form>
</div>


<c:choose>
    <c:when test="${not empty requestScope.voituresDisponibles}">
        <div class="car-card-grid">
            <c:forEach var="voiture" items="${requestScope.voituresDisponibles}">
                <div class="car-card">
                    <%-- Placeholder pour l'image de la voiture. Tu devras gérer les vraies images. --%>
                    <%-- Si tu as un champ 'photoUrl' dans ton entité Voiture, utilise-le ici: ${voiture.photoUrl} --%>
                    <img src="https://placehold.co/400x250/3b82f6/ffffff?text=<c:out value="${voiture.marque}"/>+<c:out value="${voiture.modele}"/>"
                         alt="Image de <c:out value="${voiture.marque}"/> <c:out value="${voiture.modele}"/>"
                         class="car-card-image"
                         onerror="this.onerror=null;this.src='https://placehold.co/400x250/cccccc/333333?text=Voiture';">
                    <div class="car-card-content">
                        <h3 class="car-card-title">
                            <c:out value="${voiture.marque != null ? voiture.marque : 'Marque inconnue'}"/> 
                            <c:out value="${voiture.modele != null ? voiture.modele : 'Modèle inconnu'}"/>
                        </h3>
                        <p class="car-card-detail"><strong>Immatriculation:</strong> <c:out value="${voiture.immatriculation != null ? voiture.immatriculation : 'N/A'}"/></p>
                        <p class="car-card-detail"><strong>Kilométrage:</strong> <fmt:formatNumber value="${voiture.kilometrage}" pattern="#,##0" /> km</p>
                        <p class="car-card-detail"><strong>Places:</strong> <c:out value="${voiture.nbPlaces}"/></p>
                        <p class="car-card-detail"><strong>Carburant:</strong> <c:out value="${voiture.typeCarburant != null ? voiture.typeCarburant : 'N/A'}"/></p>
                        <p class="car-card-detail"><strong>Catégorie:</strong> <c:out value="${voiture.categorie != null ? voiture.categorie : 'N/A'}"/></p>
                        <p class="car-card-price">
                            <fmt:formatNumber value="${voiture.prixLocationJ != null ? voiture.prixLocationJ : 0}" pattern="#,##0.00" /> € / jour
                        </p>
                        <a href="<c:url value="/clientRental?action=showRentalForm&immatriculation=${voiture.immatriculation}"/>" class="car-card-button">
                            <i class="fas fa-info-circle mr-2"></i> Détails & Louer
                        </a>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:when>
    <c:otherwise>
        <p class="text-gray-600 py-4 text-center text-xl mt-8">Aucune voiture disponible correspondant à vos critères de recherche.</p>
    </c:otherwise>
</c:choose>
