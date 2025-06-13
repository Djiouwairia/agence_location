<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Liste des Clients</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="dashboard-container">
        <h2 class="text-2xl font-bold mb-6">Gestion des Clients</h2>

        <%-- Message de succès ou d'erreur --%>
        <c:if test="${not empty requestScope.message}">
            <p class="success-message">${requestScope.message}</p>
        </c:if>
        <c:if test="${not empty requestScope.error}">
            <p class="error-message">${requestScope.error}</p>
        </c:if>

        <div class="mb-4 flex justify-between items-center">
            <a href="clients?action=new" class="inline-block bg-green-600 text-white py-2 px-4 rounded-md hover:bg-green-700">Ajouter un nouveau client</a>
            
            <%-- Formulaire de recherche --%>
            <form action="clients" method="get" class="flex space-x-2">
                <input type="hidden" name="action" value="search">
                <input type="text" name="searchCin" placeholder="Rechercher par CIN" class="px-3 py-2 border rounded-md">
                <input type="text" name="searchNom" placeholder="Rechercher par Nom" class="px-3 py-2 border rounded-md">
                <button type="submit" class="bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700">Rechercher</button>
            </form>
        </div>

        <c:choose>
            <c:when test="${not empty requestScope.clients}">
                <table>
                    <thead>
                        <tr>
                            <th>CIN</th>
                            <th>Prénom</th>
                            <th>Nom</th>
                            <th>Sexe</th>
                            <th>Adresse</th>
                            <th>Email</th>
                            <th>Téléphone</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="client" items="${requestScope.clients}">
                            <tr>
                                <td>${client.cin}</td>
                                <td>${client.prenom}</td>
                                <td>${client.nom}</td>
                                <td>${client.sexe}</td>
                                <td>${client.adresse}</td>
                                <td>${client.email}</td>
                                <td>${client.telephone}</td>
                                <td>
                                    <a href="clients?action=edit&cin=${client.cin}" class="text-blue-600 hover:underline mr-2">Modifier</a>
                                    <a href="clients?action=delete&cin=${client.cin}" onclick="return confirm('Êtes-vous sûr de vouloir supprimer ce client ?');" class="text-red-600 hover:underline">Supprimer</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p>Aucun client trouvé.</p>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
