<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Soumettre une Demande de Location</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="dashboard-container">
        <h2 class="text-2xl font-bold mb-6">Soumettre une Demande de Location</h2>

        <%-- Message de succès ou d'erreur --%>
        <c:if test="${not empty requestScope.message}">
            <p class="success-message">${requestScope.message}</p>
        </c:if>
        <c:if test="${not empty requestScope.error}">
            <p class="error-message">${requestScope.error}</p>
        </c:if>

        <c:if test="${not empty requestScope.selectedVoiture}">
            <div class="dashboard-section card mb-8">
                <h3 class="card-title text-xl font-semibold mb-4">Voiture sélectionnée</h3>
                <p><strong>Immatriculation:</strong> ${requestScope.selectedVoiture.immatriculation}</p>
                <p><strong>Marque:</strong> ${requestScope.selectedVoiture.marque}</p>
                <p><strong>Modèle:</strong> ${requestScope.selectedVoiture.modele}</p>
                <p><strong>Catégorie:</strong> ${requestScope.selectedVoiture.categorie}</p>
                <p><strong>Prix par jour:</strong> <fmt:formatNumber value="${requestScope.selectedVoiture.prixLocationJ}" type="currency" currencySymbol="€" /></p>
                <p class="text-sm text-gray-600 mt-2">Le kilométrage actuel (${requestScope.selectedVoiture.kilometrage} km) sera enregistré au moment de la validation par le gestionnaire.</p>
            </div>

            <form action="locations" method="post" class="space-y-4">
                <input type="hidden" name="action" value="submitRequest">
                <input type="hidden" name="voitureImmat" value="${requestScope.selectedVoiture.immatriculation}">
                <%-- Le CIN du client est pris de la session (currentUser.getUsername()), donc pas besoin d'un champ caché --%>

                <div>
                    <label for="dateDebut" class="block text-sm font-medium text-gray-700">Date de début de location :</label>
                    <input type="date" id="dateDebut" name="dateDebut" required
                           min="<fmt:formatDate value='<%= new java.util.Date() %>' pattern='yyyy-MM-dd' />"
                           class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm">
                    <p class="text-xs text-gray-500 mt-1">La date de début ne peut pas être antérieure à aujourd'hui.</p>
                </div>
                <div>
                    <label for="nombreJours" class="block text-sm font-medium text-gray-700">Nombre de jours de location :</label>
                    <input type="number" id="nombreJours" name="nombreJours" required min="1"
                           placeholder="Entrez le nombre de jours"
                           class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm">
                </div>
                
                <button type="submit" class="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700">
                    Soumettre la Demande
                </button>
                <a href="clientDashboard" class="block text-center mt-2 text-gray-600 hover:underline">Annuler et Retourner au Tableau de Bord</a>
            </form>
        </c:if>
        <c:if test="${empty requestScope.selectedVoiture}">
            <p class="error-message">Aucune voiture sélectionnée pour la demande de location.</p>
            <a href="clientDashboard" class="block text-center mt-4 text-gray-600 hover:underline">Retour au Tableau de Bord</a>
        </c:if>
    </div>
</body>
</html>
