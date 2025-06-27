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
    <%-- AJOUTEZ CETTE LIGNE POUR FONT AWESOME --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
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
            <%-- MODIFICATION IMPORTANTE ICI : Utilisation de la nouvelle classe CSS --%>
            <div class="client-actions-group"> <%-- Remplacez "flex space-x-2" par "client-actions-group" --%>
                <a href="clients?action=new" class=" btn-primary inline-block bg-green-600 text-white py-2 px-4 rounded-md hover:bg-green-700"> <%-- Assurez-vous que mr-2 est retiré ici --%>
                    <i class="fas fa-plus-circle mr-2"></i> Ajouter un nouveau client
                </a>
                <%-- NOUVEAU BOUTON D'EXPORTATION PDF --%>
<a href="clients?action=exportPdf" class="btn-primary inline-block bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700"><i class="fas fa-download mr-2"></i> Télécharger</a>            </div>
            
            <%-- Formulaire de recherche --%>
            <form action="clients" method="get" class="flex space-x-2">
                <input type="hidden" name="action" value="search">
                <input type="text" name="searchCin" placeholder="Rechercher par CIN" class="px-3 py-2 border rounded-md">
                <input type="text" name="searchNom" placeholder="Rechercher par Nom" class="px-3 py-2 border rounded-md">
                <button type="submit" class="bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700">
                    <i class="fas fa-search mr-2"></i> Rechercher
                </button>
            </form>
        </div>

        <c:choose>
            <c:when test="${not empty requestScope.clients}">
                <%-- AJOUT DE CLASSES TAILWIND POUR UN MEILLEUR STYLE DE TABLE --%>
                <div class="overflow-x-auto bg-white rounded-lg shadow">
                    <table class="min-w-full divide-y divide-gray-200">
                        <thead class="bg-gray-50">
                            <tr>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">CIN</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Prénom</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Nom</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Sexe</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Adresse</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Téléphone</th>
                                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                            </tr>
                        </thead>
                        <tbody class="bg-white divide-y divide-gray-200">
                            <c:forEach var="client" items="${requestScope.clients}">
                                <tr>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${client.cin}</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${client.prenom}</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${client.nom}</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${client.sexe}</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${client.adresse}</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${client.email}</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${client.telephone}</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                        <a href="clients?action=edit&cin=${client.cin}" class="text-indigo-600 hover:text-indigo-900 mr-4">
                                            <i class="fas fa-edit"></i> Modifier
                                        </a>
                                        <a href="clients?action=delete&cin=${client.cin}" onclick="return confirm('Êtes-vous sûr de vouloir supprimer ce client ?');" class="text-red-600 hover:text-red-900">
                                            <i class="fas fa-trash-alt"></i> Supprimer
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <p class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-center">Aucun client trouvé.</p>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>