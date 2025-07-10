<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Voitures</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
<style>
	    body {
	
		margin: 0;
		
		}

		/* CSS pour l'arrière-plan vidéo */
		
		#video-background {
		
			position: fixed;
			
			right: 0;
			
			bottom: 0;
			
			min-width: 100%;
			
			min-height: 100%;
			
			width: auto;
			
			height: auto;
			
			z-index: -100;
			
			object-fit: cover;
	}
	
	/* CSS pour l'overlay (filtre sombre sur la vidéo pour la lisibilité) */
	
	.video-overlay {
	
		position: fixed;
		
		top: 0;
		
		left: 0;
		
		width: 100%;
		
		height: 100%;
		
		background-color: rgba(0, 0, 0, 0.5);
		
		z-index: -99;
	
	}
	</style>
</head>
<body>
    <jsp:include page="navbar.jsp"/>
 <video autoplay muted loop id="video-background">
		<source src="${pageContext.request.contextPath}/videos/video1.mp4" type="video/mp4">
		Votre navigateur ne supporte pas les vidéos HTML5.
		
	</video>
    <div class="dashboard-container">
        <h2 class="text-2xl font-bold mb-6">Gestion des Voitures</h2>

        <%-- Messages de succès ou d'erreur --%>
        <c:if test="${not empty requestScope.message}">
            <p class="success-message">${requestScope.message}</p>
        </c:if>
        <c:if test="${not empty requestScope.error}">
            <p class="error-message">${requestScope.error}</p>
        </c:if>

        <div class="flex justify-between items-center mb-4">
            <%-- DÉBUT DE LA MODIFICATION ICI --%>
            <a href="voitures?action=new" class="btn-primary inline-block bg-green-600 text-white py-2 px-4 rounded-md hover:bg-green-700">
                <i class="fas fa-plus-circle mr-2"></i>Ajouter une voiture
            </a>
            <%-- FIN DE LA MODIFICATION --%>
            <form action="voitures" method="get" class="flex space-x-2">
                <input type="hidden" name="action" value="search">
                <input type="text" name="marque" placeholder="Rechercher par marque" class="form-input">
                <input type="number" name="kilometrageMax" placeholder="Kilométrage max" class="form-input" step="0.1">
                <input type="number" name="anneeMiseCirculationMin" placeholder="Année min (circ.)" class="form-input">
                <select name="typeCarburant" class="form-select">
                    <option value="">Type Carburant</option>
                    <option value="Essence">Essence</option>
                    <option value="Diesel">Diesel</option>
                    <option value="Électrique">Électrique</option>
                    <option value="Hybride">Hybride</option>
                </select>
                <select name="categorie" class="form-select">
                    <option value="">Catégorie</option>
                    <option value="Compacte">Compacte</option>
                    <option value="SUV">SUV</option>
                    <option value="Berline">Berline</option>
                    <option value="Luxe">Luxe</option>
                    <option value="Utilitaire">Utilitaire</option>
                    <option value="Sportive">Sportive</option>
                </select>
                <select name="statut" class="form-select">
                    <option value="">Statut</option>
                    <option value="Disponible">Disponible</option>
                    <option value="Louee">Louée</option>
                    <option value="En maintenance">En maintenance</option>
                </select>
                <button type="submit" class="btn-secondary">
                    <i class="fas fa-search mr-2"></i> Rechercher
                </button>
            </form>
        </div>

        <div class="overflow-x-auto bg-white rounded-lg shadow">
            <table class="min-w-full divide-y divide-gray-200">
                <thead class="bg-gray-50">
                    <tr>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Immatriculation</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Marque</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Modèle</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Places</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date Circ.</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Kilométrage</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Carburant</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Catégorie</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Prix/Jour</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Statut</th>
                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                    </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                    <c:choose>
                        <c:when test="${not empty requestScope.voitures}">
                            <c:forEach var="voiture" items="${requestScope.voitures}">
                                <tr>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${voiture.immatriculation}</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${voiture.marque}</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${voiture.modele}</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${voiture.nbPlaces}</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${voiture.dateMiseCirculation}</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${voiture.kilometrage} km</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${voiture.typeCarburant}</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${voiture.categorie}</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${voiture.prixLocationJ} €</td>
                                    <td class="px-6 py-4 whitespace-nowrap text-sm">
                                        <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full
                                            <c:choose>
                                                <c:when test="${voiture.statut eq 'Disponible'}">bg-green-100 text-green-800</c:when>
                                                <c:when test="${voiture.statut eq 'Louee'}">bg-yellow-100 text-yellow-800</c:when>
                                                <c:when test="${voiture.statut eq 'En maintenance'}">bg-red-100 text-red-800</c:when>
                                                <c:otherwise>bg-gray-100 text-gray-800</c:otherwise>
                                            </c:choose>
                                        ">
                                            ${voiture.statut}
                                        </span>
                                    </td>
                                    <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                        <a href="voitures?action=edit&immatriculation=${voiture.immatriculation}" class="text-indigo-600 hover:text-indigo-900 mr-4">
                                            <i class="fas fa-edit"></i>
                                        </a>
                                        <a href="voitures?action=delete&immatriculation=${voiture.immatriculation}" class="text-red-600 hover:text-red-900"
                                           onclick="return confirm('Êtes-vous sûr de vouloir supprimer cette voiture ?');">
                                            <i class="fas fa-trash-alt"></i>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="11" class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-center">
                                    Aucune voiture trouvée.
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>