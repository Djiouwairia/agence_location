<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:if test="${client != null}">Modifier</c:if><c:else>Ajouter</c:else> Client</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="dashboard-container">
        <h2 class="text-2xl font-bold mb-6"><c:if test="${client != null}">Modifier</c:if><c:else>Ajouter</c:else> un Client</h2>

        <%-- Message de succès ou d'erreur --%>
        <c:if test="${not empty requestScope.message}">
            <p class="success-message">${requestScope.message}</p>
        </c:if>
        <c:if test="${not empty requestScope.error}">
            <p class="error-message">${requestScope.error}</p>
        </c:if>

        <form action="clients" method="post" class="space-y-4">
            <c:if test="${client != null}">
                <input type="hidden" name="action" value="update">
            </c:if>
            <c:if test="${client == null}">
                <input type="hidden" name="action" value="add">
            </c:if>

            <div>
                <label for="cin" class="block text-sm font-medium text-gray-700">CIN :</label>
                <input type="text" id="cin" name="cin" value="${client.cin}" required
                       <c:if test="${client != null}">readonly</c:if>
                       class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm">
                <c:if test="${client != null}"><p class="text-xs text-gray-500 mt-1">Le CIN ne peut pas être modifié.</p></c:if>
            </div>
            <div>
                <label for="prenom" class="block text-sm font-medium text-gray-700">Prénom :</label>
                <input type="text" id="prenom" name="prenom" value="${client.prenom}" required
                       class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm">
            </div>
            <div>
                <label for="nom" class="block text-sm font-medium text-gray-700">Nom :</label>
                <input type="text" id="nom" name="nom" value="${client.nom}" required
                       class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm">
            </div>
            <div>
                <label for="sexe" class="block text-sm font-medium text-gray-700">Sexe :</label>
                <select id="sexe" name="sexe" class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm">
                    <option value="">Sélectionner</option>
                    <option value="Homme" <c:if test="${client.sexe eq 'Homme'}">selected</c:if>>Homme</option>
                    <option value="Femme" <c:if test="${client.sexe eq 'Femme'}">selected</c:if>>Femme</option>
                </select>
            </div>
            <div>
                <label for="adresse" class="block text-sm font-medium text-gray-700">Adresse :</label>
                <input type="text" id="adresse" name="adresse" value="${client.adresse}"
                       class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm">
            </div>
            <div>
                <label for="email" class="block text-sm font-medium text-gray-700">Email :</label>
                <input type="email" id="email" name="email" value="${client.email}"
                       class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm">
            </div>
            <div>
                <label for="telephone" class="block text-sm font-medium text-gray-700">Téléphone :</label>
                <input type="text" id="telephone" name="telephone" value="${client.telephone}"
                       class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm">
            </div>

            <button type="submit" class="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700">
                <c:if test="${client != null}">Modifier</c:if><c:else>Ajouter</c:else> Client
            </button>
            <a href="clients" class="block text-center mt-2 text-gray-600 hover:underline">Retour à la liste</a>
        </form>
    </div>
</body>
</html>
