<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Liste des Voitures</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="dashboard-container">
        <h2 class="text-2xl font-bold mb-6">Gestion des Voitures</h2>

        <%-- Message de succès ou d'erreur --%>
        <c:if test="${not empty requestScope.message}">
            <p class="success-message">${requestScope.message}</p>
        </c:if>
        <c:if test="${not empty requestScope.error}">
            <p class="error-message">${requestScope.error}</p>
        </c:if>

        <div class="mb-4 flex justify-between items-center">
            <a href="voitures?action=new" class="inline-block bg-green-600 text-white py-2 px-4 rounded-md hover:bg-green-700">Ajouter une nouvelle voiture</a>
            
            <%-- Formulaire de recherche --%>
            <form action="voitures" method="get" class="flex space-x-2 items-end">
                <input type="hidden" name="action" value="search">
                <div>
                    <label for="marque" class="block text-sm font-medium text-gray-700">Marque :</label>
                    <input type="text" id="marque" name="marque" placeholder="Marque" class="px-3 py-2 border rounded-md">
                </div>
                <div>
                    <label for="kilometrageMax" class="block text-sm font-medium text-gray-700">Kilométrage Max :</label>
                    <input type="number" id="kilometrageMax" name="kilometrageMax" placeholder="Km Max" class="px-3 py-2 border rounded-md">
                </div>
                <div>
                    <label for="anneeMiseCirculationMin" class="block text-sm font-medium text-gray-700">Année Min :</label>
                    <input type="number" id="anneeMiseCirculationMin" name="anneeMiseCirculationMin" placeholder="Année Min" class="px-3 py-2 border rounded-md">
                </div>
                <div>
                    <label for="typeCarburant" class="block text-sm font-medium text-gray-700">Carburant :</label>
                    <select id="typeCarburant" name="typeCarburant" class="px-3 py-2 border rounded-md">
                        <option value="">Tous</option>
                        <option value="Essence">Essence</option>
                        <option value="Diesel">Diesel</option>
                        <option value="Électrique">Électrique</option>
                        <option value="Hybride">Hybride</option>
                    </select>
                </div>
                <div>
                    <label for="categorie" class="block text-sm font-medium text-gray-700">Catégorie :</label>
                    <select id="categorie" name="categorie" class="px-3 py-2 border rounded-md">
                        <option value="">Toutes</option>
                        <option value="Compacte">Compacte</option>
                        <option value="SUV">SUV</option>
                        <option value="Berline">Berline</option>
                        <option value="Luxe">Luxe</option>
                        <option value="Utilitaire">Utilitaire</option>
                    </select>
                </div>
                <div>
                    <label for="statut" class="block text-sm font-medium text-gray-700">Statut :</label>
                    <select id="statut" name="statut" class="px-3 py-2 border rounded-md">
                        <option value="">Tous</option>
                        <option value="Disponible">Disponible</option>
                        <option value="Louee">Louée</option>
                        <option value="En maintenance">En maintenance</option>
                    </select>
                </div>
                <button type="submit" class="bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700">Rechercher</button>
            </form>
        </div>

        <c:choose>
            <c:when test="${not empty requestScope.voitures}">
                <table>
                    <thead>
                        <tr>
                            <th>Immatriculation</th>
                            <th>Places</th>
                            <th>Marque</th>
                            <th>Modèle</th>
                            <th>Date MEC</th>
                            <th>Kilométrage</th>
                            <th>Carburant</th>
                            <th>Catégorie</th>
                            <th>Prix/Jour</th>
                            <th>Statut</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="voiture" items="${requestScope.voitures}">
                            <tr>
                                <td>${voiture.immatriculation}</td>
                                <td>${voiture.nbPlaces}</td>
                                <td>${voiture.marque}</td>
                                <td>${voiture.modele}</td>
                                <td><fmt:formatDate value="${voiture.dateMiseCirculation}" pattern="dd/MM/yyyy" /></td>
                                <td><fmt:formatNumber value="${voiture.kilometrage}" pattern="#,##0.0" /> km</td>
                                <td>${voiture.typeCarburant}</td>
                                <td>${voiture.categorie}</td>
                                <td><fmt:formatNumber value="${voiture.prixLocationJ}" type="currency" currencySymbol="€" /></td>
                                <td>${voiture.statut}</td>
                                <td>
                                    <a href="voitures?action=edit&immatriculation=${voiture.immatriculation}" class="text-blue-600 hover:underline mr-2">Modifier</a>
                                    <a href="voitures?action=delete&immatriculation=${voiture.immatriculation}" onclick="return confirm('Êtes-vous sûr de vouloir supprimer cette voiture ?');" class="text-red-600 hover:underline">Supprimer</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p>Aucune voiture trouvée</p>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
