<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <c:choose>
            <c:when test="${client != null}"> <%-- Utilisez 'client' car c'est l'attribut que la servlet met --%>
                Modifier un Client
            </c:when>
            <c:otherwise>
                Ajouter un Client
            </c:otherwise>
        </c:choose>
    </title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="dashboard-container">
        <h1 class="text-2xl font-bold mb-6">
            <c:choose>
                <c:when test="${client != null}"> <%-- Utilisez 'client' --%>
                    Modifier le Client
                </c:when>
                <c:otherwise>
                    Ajouter un Nouveau Client
                </c:otherwise>
            </c:choose>
        </h1>

        <%-- Message de succès ou d'erreur --%>
        <%-- Récupère les messages de succès/erreur de la requête (mis par la servlet après redirection POST->GET) --%>
        <c:if test="${not empty requestScope.message}">
            <p class="success-message">${requestScope.message}</p>
        </c:if>
        <c:if test="${not empty requestScope.error}">
            <p class="error-message">${requestScope.error}</p>
        </c:if>

        <form action="<c:url value="/clients"/>" method="post" class="space-y-4">
            <%-- Correction ici : Définir l'action en fonction de si c'est un ajout ou une mise à jour --%>
            <c:if test="${client != null}"> <%-- Si 'client' existe, c'est une modification --%>
                <input type="hidden" name="action" value="update">
            </c:if>
            <c:if test="${client == null}"> <%-- Si 'client' est null, c'est un ajout --%>
                <input type="hidden" name="action" value="add">
            </c:if>

            <div class="form-group">
                <label for="cin">CIN :</label>
                <input type="text" id="cin" name="cin" value="${client.cin}" required
                       <c:if test="${client != null}">readonly</c:if> <%-- CIN non modifiable si édition --%>
                       class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring focus:border-blue-300">
                <c:if test="${client != null}">
                    <p class="text-sm text-gray-500 mt-1">Le CIN ne peut pas être modifié.</p>
                </c:if>
            </div>

            <div class="form-group">
                <label for="permis">Permis :</label>
                <input type="text" id="permis" name="permis" value="${client.permis}" required
                       class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring focus:border-blue-300">
            </div>

            <div class="form-group">
                <label for="prenom">Prénom :</label>
                <input type="text" id="prenom" name="prenom" value="${client.prenom}" required
                       class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring focus:border-blue-300">
            </div>

            <div class="form-group">
                <label for="nom">Nom :</label>
                <input type="text" id="nom" name="nom" value="${client.nom}" required
                       class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring focus:border-blue-300">
            </div>

            <div class="form-group">
                <label for="sexe">Sexe :</label>
                <select id="sexe" name="sexe" required
                        class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring focus:border-blue-300">
                    <option value="">Sélectionner</option>
                    <option value="Homme" <c:if test="${client.sexe == 'Homme'}">selected</c:if>>Homme</option>
                    <option value="Femme" <c:if test="${client.sexe == 'Femme'}">selected</c:if>>Femme</option>
                </select>
            </div>

            <div class="form-group">
                <label for="adresse">Adresse :</label>
                <input type="text" id="adresse" name="adresse" value="${client.adresse}"
                       class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring focus:border-blue-300">
            </div>

            <div class="form-group">
                <label for="telephone">Téléphone :</label>
                <input type="text" id="telephone" name="telephone" value="${client.telephone}"
                       class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring focus:border-blue-300">
            </div>

            <div class="form-group">
                <label for="email">Email :</label>
                <input type="email" id="email" name="email" value="${client.email}"
                       class="w-full px-3 py-2 border rounded-md focus:outline-none focus:ring focus:border-blue-300">
            </div>

            <div class="flex justify-end space-x-4 mt-6">
                <button type="submit" class="bg-green-600 text-white py-2 px-6 rounded-md hover:bg-green-700">
                    Enregistrer
                </button>
                <a href="<c:url value="/clients"/>" class="bg-gray-500 text-white py-2 px-6 rounded-md hover:bg-gray-600">
                    Annuler
                </a>
            </div>
        </form>
    </div>
</body>
</html>
