<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Enregistrer Retour Voiture</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="dashboard-container">
        <h2 class="text-2xl font-bold mb-6">Enregistrer le Retour d'une Voiture</h2>

        <%-- Message de succès ou d'erreur --%>
        <c:if test="${not empty requestScope.message}">
            <p class="success-message">${requestScope.message}</p>
        </c:if>
        <c:if test="${not empty requestScope.error}">
            <p class="error-message">${requestScope.error}</p>
        </c:if>

        <c:if test="${requestScope.locationToReturn != null}">
            <form action="locations" method="post" class="space-y-4">
                <input type="hidden" name="action" value="return">
                <input type="hidden" name="locationId" value="${locationToReturn.id}">

                <div class="dashboard-section">
                    <h3>Détails de la Location</h3>
                    <p><strong>ID Location :</strong> ${locationToReturn.id}</p>
                    <p><strong>Client :</strong> ${locationToReturn.client.prenom} ${locationToReturn.client.nom} (CIN: ${locationToReturn.client.cin})</p>
                    <p><strong>Voiture :</strong> ${locationToReturn.voiture.marque} ${locationToReturn.voiture.modele} (Immat: ${locationToReturn.voiture.immatriculation})</p>
                    <%-- ***** MODIFICATION ICI ***** --%>
                    <p><strong>Date Début :</strong> <fmt:formatDate value="${utilDateDebut}" pattern="dd/MM/yyyy" /></p>
                    <p><strong>Date Retour Prévue :</strong> <fmt:formatDate value="${utilDateRetourPrevue}" pattern="dd/MM/yyyy" /></p>
                    <%-- ***** FIN DE LA MODIFICATION ***** --%>
                    <p><strong>Kilométrage au départ :</strong> ${locationToReturn.kilometrageDepart} km</p>
                    <p><strong>Kilométrage actuel voiture :</strong> ${locationToReturn.voiture.kilometrage} km</p>
                </div>

                <div>
                    <label for="kilometrageRetour" class="block text-sm font-medium text-gray-700">Nouveau Kilométrage (au retour) :</label>
                    <input type="number" id="kilometrageRetour" name="kilometrageRetour" required step="0.1" min="${locationToReturn.voiture.kilometrage}"
                           class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm">
                    <p class="text-xs text-gray-500 mt-1">Le kilométrage doit être supérieur ou égal au kilométrage actuel de la voiture.</p>
                </div>

                <button type="submit" class="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700">
                    Enregistrer le Retour
                </button>
                <a href="locations" class="block text-center mt-2 text-gray-600 hover:underline">Annuler</a>
            </form>
        </c:if>
        <c:if test="${requestScope.locationToReturn == null}">
            <p>Location non trouvée ou ne peut pas être retournée.</p>
            <a href="locations" class="block text-center mt-4 text-gray-600 hover:underline">Retour à la liste des locations</a>
        </c:if>
    </div>
</body>
</html>