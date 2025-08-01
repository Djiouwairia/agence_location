<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%-- Importez la classe LocalDate pour l'utiliser directement dans les conditions JSTL --%>
<%@ page import="java.time.LocalDate" %>
<% LocalDate now = LocalDate.now(); request.setAttribute("now", now); %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tableau de Bord Chef d'Agence</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <%-- Ajout d'un paramètre de version pour forcer le rechargement du CSS --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=<%= System.currentTimeMillis() %>">
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
        /* Styles pour masquer/afficher les éléments */
        .hidden-element {
            display: none !important; /* Utiliser !important pour s'assurer que le style est appliqué */
        }
        /* Styles pour les boutons d'action */
        .action-button {
            padding: 0.5rem 0.75rem;
            border-radius: 0.375rem; /* rounded-md */
            font-weight: 600; /* font-semibold */
            transition: background-color 0.2s ease-in-out;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
        }
        .action-button.edit {
            background-color: #3B82F6; /* blue-500 */
            color: white;
        }
        .action-button.edit:hover {
            background-color: #2563EB; /* blue-600 */
        }
        .action-button.delete {
            background-color: #EF4444; /* red-500 */
            color: white;
        }
        .action-button.delete:hover {
            background-color: #DC2626; /* red-600 */
        }
        /* Styles pour le modal de confirmation */
        .modal-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0, 0, 0, 0.5);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 1000;
        }
        .modal-content {
            background-color: white;
            padding: 2rem;
            border-radius: 0.5rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            text-align: center;
            max-width: 400px;
            width: 90%;
        }
        .modal-buttons {
            display: flex;
            justify-content: center;
            gap: 1rem;
            margin-top: 1.5rem;
        }

        /* --- STYLES DE DÉBOGAGE TEMPORAIRES (À RETIRER UNE FOIS LE PROBLÈME RÉSOLU) --- */
        /*
        #mostRentedCarsTableBody tr,
        #mostRentedCarsTableBody td,
        #mostRentedCarsTableBody th {
            border: 1px solid red !important;
            padding: 8px !important;
            background-color: rgba(255, 255, 0, 0.1) !important;
            color: black !important;
        }
        */
        /* --- FIN DES STYLES DE DÉBOGAGE --- */
    </style>
</head>
<body>
	<video autoplay muted loop id="video-background">
		<source src="${pageContext.request.contextPath}/videos/video5.mp4" type="video/mp4">
		Votre navigateur ne supporte pas les vidéos HTML5.
		
	</video>

<div class="video-overlay"></div>
    <%-- Inclusion de la barre de navigation existante (navbar.jsp) --%>
    <jsp:include page="navbar.jsp"/>

    <%-- Contenu principal de l'application --%>
    <div class="content-area" id="contentArea">
        <main class="main-content-card">
            <%-- Barre de Navigation Horizontale (pour la navigation interne du tableau de bord) --%>
            <nav class="horizontal-nav flex justify-center mb-6 bg-gray-100 p-2 rounded-lg shadow-sm">
                <a href="#" class="h-nav-link text-gray-700 hover:bg-gray-200 px-4 py-2 rounded-md font-semibold transition-colors flex items-center gap-2" data-content-id="overviewDetails">
                    <i class="fas fa-chart-pie"></i> Vue d'ensemble
                </a>
                <a href="#" class="h-nav-link text-gray-700 hover:bg-gray-200 px-4 py-2 rounded-md font-semibold transition-colors flex items-center gap-2" data-content-id="parkingDetails">
                    <i class="fas fa-warehouse"></i> Parking
                </a>
                <a href="#" class="h-nav-link text-gray-700 hover:bg-gray-200 px-4 py-2 rounded-md font-semibold transition-colors flex items-center gap-2" data-content-id="carsDetails">
                    <i class="fas fa-car-side"></i> Voitures
                </a>
                <a href="#" class="h-nav-link text-gray-700 hover:bg-gray-200 px-4 py-2 rounded-md font-semibold transition-colors flex items-center gap-2" data-content-id="financialDetails">
                    <i class="fas fa-dollar-sign"></i> Bilan Financier
                </a>
                <%-- NOUVEL ONGLET : Gestionnaires --%>
                <a href="#" class="h-nav-link text-gray-700 hover:bg-gray-200 px-4 py-2 rounded-md font-semibold transition-colors flex items-center gap-2" data-content-id="managersDetails">
                    <i class="fas fa-users-cog"></i> Gestionnaires
                </a>
            </nav>

            <%-- Conteneur pour le contenu dynamique des onglets --%>
            <div id="dynamicContentContainer">

                <%-- Section: Vue d'ensemble (Dashboard Overview) --%>
                <div id="overviewDetails" class="dynamic-content">
                    <h1 class="text-3xl font-bold text-gray-800 mb-6 text-center">Vue d'ensemble de l'Agence</h1>

                    <div class="grid grid-cols-1 md:grid-cols-2 lg:col-span-2 gap-6 mb-8">
                        <div class="md:col-span-1 flex flex-col gap-6">
                            <%-- Carte: Nombre total de voitures --%>
                            <div class="card">
                                <h3 class="card-title">Nombre total de voitures</h3>
                                <p class="card-value text-gray-800" id="totalCarsValue">
                                    <c:out value="${requestScope.nombreTotalVoitures != null ? requestScope.nombreTotalVoitures : 'N/A'}"/>
                                </p>
                            </div>
                            <%-- Carte: Voitures Disponibles --%>
                            <div class="card">
                                <h3 class="card-title">Voitures Disponibles</h3>
                                <p class="card-value text-green-600" id="availableCarsValue">
                                    <c:out value="${requestScope.nombreVoituresDisponibles != null ? requestScope.nombreVoituresDisponibles : 'N/A'}"/>
                                </p>
                            </div>
                            <%-- Carte: Voitures Louées --%>
                            <div class="card">
                                <h3 class="card-title">Voitures Louées</h3>
                                <p class="card-value text-yellow-600" id="rentedCarsValue">
                                    <c:out value="${requestScope.nombreVoituresLouees != null ? requestScope.nombreVoituresLouees : 'N/A'}"/>
                                </p>
                            </div>
                            <%-- Carte: Demandes en Attente --%>
                            <div class="card">
                                <h3 class="card-title">Demandes en Attente</h3>
                                <p class="card-value text-red-600" id="pendingRequestsValue">
                                    <c:out value="${requestScope.pendingRequestsCount != null ? requestScope.pendingRequestsCount : 'N/A'}"/>
                                </p>
                            </div>
                            
                            
                            
                         
                            
                        </div>

                        <div class="card md:col-span-1 chart-container">
                             <canvas id="carStatusPieChart"></canvas>
                             <p class="js-error-message hidden" id="pieChartError"></p>
                        </div>
                        
                        
                        
                         <%-- Nouvelle Carte: Capacité du Parking (ajoutée ici) --%>
                            <div class="card">
                                <h3 class="card-title">Capacité du Parking</h3>
                                <p class="card-value text-blue-600">
                                    50
                                </p>
                            </div>
                    </div>

                    <%-- NOUVELLE SECTION: Voitures Disponibles dans le Parc (ajoutée ici) --%>
                    <div class="flex items-center justify-between mt-8 mb-4">
                        <h2 class="text-2xl font-semibold text-gray-700">Voitures Disponibles dans le Parc</h2>
                        
                    </div>
                    <div class="overflow-x-auto rounded-lg shadow mb-8">
                        <table>
                            <thead>
                                <tr>
                                    <th>Immatriculation</th>
                                    <th>Marque</th>
                                    <th>Modèle</th>
                                    <th>Catégorie</th>
                                    <th class="text-center">Prix/Jour</th>
                                </tr>
                            </thead>
                            <tbody class="text-gray-700 text-sm">
                                <c:choose>
                                    <c:when test="${not empty requestScope.voituresDisponiblesDansParking}">
                                        <c:forEach var="voiture" items="${requestScope.voituresDisponiblesDansParking}">
                                            <tr>
                                                <td class="whitespace-nowrap">${voiture.immatriculation}</td>
                                                <td>${voiture.marque}</td>
                                                <td>${voiture.modele}</td>
                                                <td>${voiture.categorie}</td>
                                                <td class="text-center"><fmt:formatNumber value="${voiture.prixLocationJ}" pattern="#,##0.00" /> €</td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="5" class="text-center text-gray-500 py-4">Aucune voiture disponible dans le parc.</td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>

                    <%-- Voitures actuellement louées --%>
                    <div class="flex items-center justify-between mt-8 mb-4">
                        <h2 class="text-2xl font-semibold text-gray-700">Voitures Actuellement Louées</h2>
                        
                    </div>
                    <div class="overflow-x-auto rounded-lg shadow">
                        <table>
                            <thead>
                                <tr>
                                    <th>Immatriculation</th>
                                    <th>Marque</th>
                                    <th>Modèle</th>
                                    <th>Locataire (CIN)</th>
                                    <th>Locataire (Nom)</th>
                                    <th>Date Début</th>
                                    <th>Jours</th>
                                </tr>
                            </thead>
                            <tbody class="text-gray-700 text-sm">
                                <c:choose>
                                    <c:when test="${not empty requestScope.voituresLoueesAvecInfosLocataires}">
                                        <c:forEach var="location" items="${requestScope.voituresLoueesAvecInfosLocataires}">
                                            <tr>
                                                <td class="whitespace-nowrap">${location.voiture.immatriculation}</td>
                                                <td>${location.voiture.marque}</td> <%-- CORRECTION ICI: de 'voitre' à 'voiture' --%>
                                                <td>${location.voiture.modele}</td>
                                                <td>${location.client.cin}</td>
                                                <td>${location.client.prenom} ${location.client.nom}</td>
                                                <td><fmt:formatDate value="${location.legacyDateDebut}" pattern="dd/MM/yyyy"/></td>
                                                <td>${location.nombreJours}</td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="7" class="text-center text-gray-500 py-4">Aucune voiture actuellement en location.</td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>

                    <%-- Top 5 Voitures Les Plus Recherchées (pour la vue d'ensemble) --%>
                    <h2 class="text-2xl font-semibold text-gray-700 mt-8 mb-4">Top 5 Voitures Les Plus Recherchées</h2>
                    <div class="overflow-x-auto rounded-lg shadow">
                        <table>
                            <thead>
                                <tr>
                                    <th>Immatriculation</th>
                                    <th>Marque</th>
                                    <th>Modèle</th>
                                    </tr>
                            </thead>
                            <tbody class="text-gray-700 text-sm">
                                <c:choose>
                                    <c:when test="${not empty requestScope.voituresPlusRecherches}">
                                        <c:forEach var="voiture" items="${requestScope.voituresPlusRecherches}">
                                            <tr>
                                                <td class="whitespace-nowrap">${voiture.immatriculation}</td>
                                                <td>${voiture.marque}</td>
                                                <td>${voiture.modele}</td>
                                                </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="3" class="text-center text-gray-500 py-4">Aucune donnée disponible.</td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <%-- Section: Parking (Détails) --%>
                <div id="parkingDetails" class="dynamic-content">
                    <h1 class="text-3xl font-bold text-gray-800 mb-6 text-center">Parking des Voitures(Capacite:50)</h1>
                    <p class="mb-4 text-gray-600"></p>

                    <div class="flex items-center justify-between mt-8 mb-4">
                        <h2 class="text-2xl font-semibold text-gray-700">Voitures Disponibles pour Location</h2>
                        <button  title="Télécharger la liste des voitures disponibles"
                                >
 							<a href="locations?action=exportList" class="btn-primary bg-blue-600 hover:bg-blue-700">
                          <i class="fas fa-file-pdf mr-2"></i> 
                         </a></button>
                    </div>
                    <div class="overflow-x-auto rounded-lg shadow">
                        <table>
                            <thead>
                                <tr>
                                    <th>Immatriculation</th>
                                    <th>Marque</th>
                                    <th>Modèle</th>
                                    <th>Statut</th>
                                    <th>Catégorie</th>
                                    <th class="text-center">Prix/Jour</th>
                                </tr>
                            </thead>
                            <tbody class="text-gray-700 text-sm">
                                <c:choose>
                                    <c:when test="${not empty requestScope.voituresDisponiblesDansParking}">
                                        <c:forEach var="voiture" items="${requestScope.voituresDisponiblesDansParking}">
                                            <tr>
                                                <td class="whitespace-nowrap">${voiture.immatriculation}</td>
                                                <td>${voiture.marque}</td>
                                                <td>${voiture.modele}</td>
                                                <td>${voiture.statut}</td>
                                                <td>${voiture.categorie}</td>
                                                <td class="text-center"><fmt:formatNumber value="${voiture.prixLocationJ}" pattern="#,##0.00" /> €</td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="6" class="text-center text-gray-500 py-4">Aucune voiture disponible dans le parking.</td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <%-- Section: Voitures (Les plus louées avec filtres) --%>
                <div id="carsDetails" class="dynamic-content">
                    <h1 class="text-3xl font-bold text-gray-800 mb-6 text-center">Voitures les Plus Louées</h1>
                    <p class="mb-4 text-gray-600">Consultez les véhicules les plus demandés en filtrant par nombre et par période.</p>

                    <div class="filter-form-grid">
                        <div class="filter-group">
                            <label for="topNCarsFilter">Top N :</label>
                            <select id="topNCarsFilter" class="w-full">
                                <option value="5">Top 5</option>
                                <option value="10">Top 10</option>
                                <option value="20">Top 20</option>
                                <option value="50">Top 50</option>
                            </select>
                        </div>
                        <div class="filter-group">
                            <label for="periodCarsFilter">Période :</label>
                            <select id="periodCarsFilter" class="w-full">
                                <option value="all">Depuis toujours</option>
                                <option value="3months">3 derniers mois</option>
                                <option value="6months">6 derniers mois</option>
                                <option value="currentYear">Année en cours</option>
                            </select>
                        </div>
                        <button id="applyCarsFilter" class="filter-button mt-auto">Appliquer Filtre</button>
                    </div>

                    <div class="overflow-x-auto rounded-lg shadow">
                        <table>
                            <thead>
                                <tr>
                                    <th>Immatriculation</th>
                                    <th>Marque</th>
                                    <th>Modèle</th>
                                    <th class="text-center">Nombre de locations</th>
                                </tr>
                            </thead>
                            <tbody id="mostRentedCarsTableBody" class="text-gray-700 text-sm">
                                <%-- Le contenu sera généré par JavaScript --%>
                                <tr><td colspan="4" class="text-center text-gray-500 py-4">Chargement des données...</td></tr>
                            </tbody>
                        </table>
                        <p class="js-error-message hidden" id="carsFilterError"></p>
                    </div>
                </div>

                <%-- Section: Bilan Financier --%>
                <div id="financialDetails" class="dynamic-content">
                    <h1 class="text-3xl font-bold text-gray-800 mb-6 text-center">Rapport Financier Détaillé</h1>
                    <p class="mb-4 text-gray-600">Consultez les bilans financiers pour différentes périodes et mois.</p>

                    <h2 class="text-2xl font-semibold text-gray-700 mt-8 mb-4">Évolution des Revenus</h2>
                    <div class="filter-form-grid">
                        <div class="filter-group col-span-full">
                            <label for="periodFinancialChart">Période du graphique :</label>
                            <select id="periodFinancialChart" class="w-full">
                                <option value="3months">3 derniers mois</option>
                                <option value="6months">6 derniers mois</option>
                                <option value="currentYear">Année en cours</option>
                                <option value="all">Depuis toujours</option>
                            </select>
                        </div>
                    </div>
                    <div class="chart-container">
                        <canvas id="financialChart"></canvas>
                        <p class="js-error-message hidden" id="financialChartError"></p>
                    </div>

                    <h2 class="text-2xl font-semibold text-gray-700 mt-8 mb-4">Bilan Financier Mensuel Spécifique</h2>
                    <div class="filter-form-grid">
                        <div class="filter-group">
                            <label for="monthSelector">Mois :</label>
                            <select id="monthSelector" class="w-full">
                                <option value="1" <c:if test="${param.month == '1' || (empty param.month && now.monthValue == 1)}">selected</c:if>>Janvier</option>
                                <option value="2" <c:if test="${param.month == '2' || (empty param.month && now.monthValue == 2)}">selected</c:if>>Février</option>
                                <option value="3" <c:if test="${param.month == '3' || (empty param.month && now.monthValue == 3)}">selected</c:if>>Mars</option>
                                <option value="4" <c:if test="${param.month == '4' || (empty param.month && now.monthValue == 4)}">selected</c:if>>Avril</option>
                                <option value="5" <c:if test="${param.month == '5' || (empty param.month && now.monthValue == 5)}">selected</c:if>>Mai</option>
                                <option value="6" <c:if test="${param.month == '6' || (empty param.month && now.monthValue == 6)}">selected</c:if>>Juin</option>
                                <option value="7" <c:if test="${param.month == '7' || (empty param.month && now.monthValue == 7)}">selected</c:if>>Juillet</option>
                                <option value="8" <c:if test="${param.month == '8' || (empty param.month && now.monthValue == 8)}">selected</c:if>>Août</option>
                                <option value="9" <c:if test="${param.month == '9' || (empty param.month && now.monthValue == 9)}">selected</c:if>>Septembre</option>
                                <option value="10" <c:if test="${param.month == '10' || (empty param.month && now.monthValue == 10)}">selected</c:if>>Octobre</option>
                                <option value="11" <c:if test="${param.month == '11' || (empty param.month && now.monthValue == 11)}">selected</c:if>>Novembre</option>
                                <option value="12" <c:if test="${param.month == '12' || (empty param.month && now.monthValue == 12)}">selected</c:if>>Décembre</option>
                            </select>
                        </div>
                        <div class="filter-group">
                            <label for="yearSelector">Année :</label>
                            <input type="number" id="yearSelector" value="${param.year != null ? param.year : now.year}" class="w-full">
                        </div>
                        <button id="applyMonthlyReportFilter" class="filter-button mt-auto">Voir Bilan</button>
                    </div>
                    <div class="card p-6 mt-4 text-left">
                        <div class="card-title">Bilan pour <span id="monthlyReportMonthDisplay"><c:out value="${requestScope.moisBilan}"/></span> <span id="monthlyReportYearDisplay"><c:out value="${requestScope.anneeBilan}"/></span></div>
                        <div class="card-value"><span id="monthlyRevenueDisplay"><fmt:formatNumber value="${requestScope.bilanMensuel.totalRevenue}" pattern="#,##0.00"/></span> €</div>
                        <p class="js-error-message hidden" id="monthlyReportError"></p>
                    </div>
                </div>

                <%-- NOUVELLE SECTION : Gestionnaires --%>
                <div id="managersDetails" class="dynamic-content">
                    <h1 class="text-3xl font-bold text-gray-800 mb-6 text-center">Gestion des Gestionnaires</h1>

                    <%-- Bouton pour afficher le formulaire d'ajout --%>
                    <div class="flex justify-end mb-4">
                        <button id="toggleAddManagerFormBtn" class="btn-secondary">
                            <i class="fas fa-plus-circle mr-2"></i> Ajouter un nouveau gestionnaire
                        </button>
                    </div>

                    <%-- Formulaire d'ajout/modification de gestionnaire (initiallement masqué) --%>
                    <div id="managerFormContainer" class="bg-white p-6 rounded-lg shadow-md mb-8 hidden-element">
                        <h2 id="managerFormTitle" class="text-2xl font-semibold text-gray-700 mb-4">Ajouter un Nouveau Gestionnaire</h2>
                        <form id="managerForm" class="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <input type="hidden" id="managerId" name="id"> <%-- Champ caché pour l'ID en cas de modification --%>
                            <div>
                                <label for="managerUsername" class="block text-sm font-medium text-gray-700">Nom d'utilisateur :</label>
                                <input type="text" id="managerUsername" name="username" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50">
                            </div>
                            <div>
                                <label for="managerPassword" class="block text-sm font-medium text-gray-700">Mot de passe :</label>
                                <input type="password" id="managerPassword" name="password" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50">
                            </div>
                            <div>
                                <label for="managerNom" class="block text-sm font-medium text-gray-700">Nom :</label>
                                <input type="text" id="managerNom" name="nom" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50">
                            </div>
                            <div>
                                <label for="managerPrenom" class="block text-sm font-medium text-gray-700">Prénom :</label>
                                <input type="text" id="managerPrenom" name="prenom" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50">
                            </div>
                            <div>
                                <label for="managerDateRecrutement" class="block text-sm font-medium text-gray-700">Date de recrutement :</label>
                                <input type="date" id="managerDateRecrutement" name="dateRecrutement" required class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50">
                            </div>
                            <div>
                                <label for="managerEmail" class="block text-sm font-medium text-gray-700">Email :</label>
                                <input type="email" id="managerEmail" name="email" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50">
                            </div>
                            <div>
                                <label for="managerTelephone" class="block text-sm font-medium text-gray-700">Téléphone :</label>
                                <input type="tel" id="managerTelephone" name="telephone" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50">
                            </div>
                            <div class="md:col-span-2">
                                <label for="managerAdresse" class="block text-sm font-medium text-gray-700">Adresse :</label>
                                <input type="text" id="managerAdresse" name="adresse" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-300 focus:ring focus:ring-indigo-200 focus:ring-opacity-50">
                            </div>
                            <div class="md:col-span-2 text-right">
                                <button type="submit" id="submitManagerBtn" class="btn-primary">Ajouter Gestionnaire</button>
                                <button type="button" id="cancelManagerFormBtn" class="btn-secondary ml-2">Annuler</button>
                            </div>
                        </form>
                        <p id="managerFormMessage" class="mt-4 text-center text-sm"></p>
                    </div>

                    <%-- Conteneur de la liste des gestionnaires --%>
                    <div id="managersListContainer">
                        <h2 class="text-2xl font-semibold text-gray-700 mt-8 mb-4">Liste des Gestionnaires</h2>
                        <div class="overflow-x-auto rounded-lg shadow">
                            <table>
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Nom d'utilisateur</th>
                                        <th>Nom</th>
                                        <th>Prénom</th>
                                        <th>Date de recrutement</th>
                                        <th>Email</th>
                                        <th>Téléphone</th>
                                        <th>Adresse</th>
                                        <th>Rôle</th>
                                        <th>Actions</th> <%-- Nouvelle colonne pour les actions --%>
                                    </tr>
                                </thead>
                                <tbody id="managersTableBody" class="text-gray-700 text-sm">
                                    <tr><td colspan="10" class="text-center text-gray-500 py-4">Chargement des gestionnaires...</td></tr>
                                </tbody>
                            </table>
                            <p class="js-error-message hidden-element" id="listManagersError"></p>
                        </div>
                    </div>
                </div>

            </div>
        </main>
    </div>

    <%-- Modal de confirmation de suppression --%>
    <div id="deleteConfirmModal" class="modal-overlay hidden-element">
        <div class="modal-content">
            <h3 class="text-xl font-bold mb-4">Confirmer la suppression</h3>
            <p class="mb-6">Êtes-vous sûr de vouloir supprimer ce gestionnaire ? Cette action est irréversible.</p>
            <div class="modal-buttons">
                <button id="confirmDeleteBtn" class="action-button delete">Supprimer</button>
                <button id="cancelDeleteBtn" class="action-button edit">Annuler</button>
            </div>
        </div>
    </div>

   <script>
        // DEBUG: Confirme que le script est chargé et exécuté
        console.log("DEBUG: Script dashboard.jsp chargé et exécuté.");

        // Déclarations globales pour les instances de graphique
        let carStatusPieChartInstance = null;
        let financialChartInstance = null;

        document.addEventListener('DOMContentLoaded', function() {
            console.log("DEBUG: DOMContentLoaded - Le DOM est entièrement chargé.");

            // Initialisation des sélecteurs mois/année au mois et année actuels (si non déjà définis par JSTL)
            const monthSelector = document.getElementById('monthSelector');
            const yearSelector = document.getElementById('yearSelector');
            const currentMonth = new Date().getMonth() + 1; // getMonth() est basé sur 0
            const currentYear = new Date().getFullYear();

            // Assurez-vous que les sélecteurs ont une valeur initiale correcte
            if (monthSelector && monthSelector.value === "") { // Si JSTL n'a pas mis de valeur
                monthSelector.value = currentMonth;
                console.log("DEBUG: monthSelector initialisé à", currentMonth);
            }
            if (yearSelector && yearSelector.value === "") { // Si JSTL n'a pas mis de valeur
                 yearSelector.value = currentYear;
                 console.log("DEBUG: yearSelector initialisé à", currentYear);
            }

            // --- Fonctions de chargement et rendu pour la section Vue d'ensemble ---
            async function fetchCarStatsAndRenderChart() {
                console.log("DEBUG: Appel de fetchCarStatsAndRenderChart()");
                const pieChartError = document.getElementById('pieChartError');
                try {
                    const response = await fetch('api/reports/car-stats');
                    if (!response.ok) {
                        const errorData = await response.json().catch(() => ({ message: response.statusText }));
                        throw new Error(`Échec de la récupération des statistiques des voitures: ${errorData.message || response.statusText}`);
                    }
                    const stats = await response.json();
                    console.log("DEBUG: Statistiques des voitures reçues:", stats);

                    document.getElementById('totalCarsValue').textContent = stats.totalCars || 0;
                    document.getElementById('availableCarsValue').textContent = stats.availableCars || 0;
                    document.getElementById('rentedCarsValue').textContent = stats.rentedCars || 0;
                    document.getElementById('pendingRequestsValue').textContent = stats.pendingRequests || 0;

                    renderCarStatusPieChart(stats.availableCars || 0, stats.rentedCars || 0, stats.pendingRequests || 0);
                    pieChartError.classList.add('hidden');
                } catch (error) {
                    console.error('ERREUR: lors de la récupération des statistiques des voitures:', error);
                    if (pieChartError) {
                        pieChartError.textContent = `Erreur lors du chargement des statistiques des voitures: ${error.message}`;
                        pieChartError.classList.remove('hidden');
                    }
                    // Mettre les valeurs à "Erreur" ou 0 si l'API échoue
                    document.getElementById('totalCarsValue').textContent = 'Erreur';
                    document.getElementById('availableCarsValue').textContent = 'Erreur';
                    document.getElementById('rentedCarsValue').textContent = 'Erreur';
                    document.getElementById('pendingRequestsValue').textContent = 'Erreur';

                    if (carStatusPieChartInstance) {
                        carStatusPieChartInstance.destroy();
                        carStatusPieChartInstance = null;
                    }
                }
            }

            function renderCarStatusPieChart(available, rented, pending) {
                console.log("DEBUG: Appel de renderCarStatusPieChart() avec Disponibles:", available, "Louées:", rented, "En Attente:", pending);
                const ctx = document.getElementById('carStatusPieChart');
                if (!ctx) {
                    console.warn('AVERTISSEMENT: Élément canvas "carStatusPieChart" non trouvé.');
                    return;
                }

                if (carStatusPieChartInstance) {
                    carStatusPieChartInstance.destroy();
                    console.log("DEBUG: Ancien graphique de statut de voiture détruit.");
                }

                const totalForChart = available + rented + pending;
                if (totalForChart === 0) {
                    console.log("DEBUG: Toutes les données de statut de voiture sont zéro, ne pas rendre le graphique.");
                    ctx.getContext('2d').clearRect(0, 0, ctx.width, ctx.height);
                    return;
                }

                carStatusPieChartInstance = new Chart(ctx.getContext('2d'), {
                    type: 'pie',
                    data: {
                        labels: ['Disponibles', 'Louées', 'En Attente'],
                        datasets: [{
                            data: [available, rented, pending],
                            backgroundColor: [
                                'rgba(34, 197, 94, 0.8)',
                                'rgba(234, 179, 8, 1)',
                                'rgba(239, 68, 68, 0.8)'
                            ],
                            borderColor: [
                                'rgba(34, 197, 94, 1)',
                                'rgba(250, 204, 21, 1)',
                                'rgba(239, 68, 68, 1)'
                            ],
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            title: {
                                display: true,
                                text: 'Proportion des Voitures (Statuts)',
                                font: { size: 16 }
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        let label = context.label || '';
                                        if (label) { label += ': '; }
                                        if (context.parsed !== null) {
                                            const value = context.parsed;
                                            const percentage = (totalForChart > 0) ? ((value / totalForChart) * 100).toFixed(1) : 0;
                                            label += `${value} voitures (${percentage}%)`;
                                        }
                                        return label;
                                    }
                                }
                            }
                        }
                    }
                });
                console.log("DEBUG: Graphique de statut de voiture rendu.");
            }

            // --- Fonctions de chargement et rendu pour la section Voitures les plus louées ---
            async function fetchAndRenderMostRentedCars() {
                console.log("DEBUG: Appel de fetchAndRenderMostRentedCars()");
                const topN = document.getElementById('topNCarsFilter').value;
                const period = document.getElementById('periodCarsFilter').value;
                const tableBody = document.getElementById('mostRentedCarsTableBody');
                const carsFilterError = document.getElementById('carsFilterError');

                if (!tableBody) {
                    console.warn("AVERTISSEMENT: Élément 'mostRentedCarsTableBody' non trouvé.");
                    return;
                }

                tableBody.innerHTML = '<tr><td colspan="4" class="text-center text-gray-500 py-4">Chargement des données...</td></tr>';
                carsFilterError.classList.add('hidden');

                try {
                    const params = new URLSearchParams();
                    params.append('limit', topN);
                    params.append('period', period);
                    const requestUrl = "api/reports/most-rented-cars?" + params.toString();

                    console.log('DEBUG: URL de la requête envoyée pour Most Rented Cars:', requestUrl);

                    const response = await fetch(requestUrl);
                    if (!response.ok) {
                        const errorData = await response.json().catch(() => ({ message: response.statusText }));
                        throw new Error(`Échec de la récupération des voitures les plus louées: ${errorData.message || response.statusText}`);
                    }
                    const cars = await response.json();
                    console.log('DEBUG: Données reçues pour Most Rented Cars:', cars);
                    console.log('DEBUG: Nombre de voitures reçues:', cars.length);

                    tableBody.innerHTML = ''; // Clear loading message
                    if (cars.length > 0) {
                        cars.forEach((car, index) => {
                            console.log(`DEBUG: Rendu de la voiture ${index + 1}: Immatriculation=${car.immatriculation}, Marque=${car.marque}, Modèle=${car.modele}, Locations=${car.rentalCount}`);
                            const tr = document.createElement('tr');

                            // Création explicite des cellules et assignation de textContent
                            const tdImmatriculation = document.createElement('td');
                            tdImmatriculation.classList.add('whitespace-nowrap');
                            tdImmatriculation.textContent = car.immatriculation;
                            tr.appendChild(tdImmatriculation);

                            const tdMarque = document.createElement('td');
                            tdMarque.textContent = car.marque;
                            tr.appendChild(tdMarque);

                            const tdModele = document.createElement('td');
                            tdModele.textContent = car.modele;
                            tr.appendChild(tdModele);

                            const tdRentalCount = document.createElement('td');
                            tdRentalCount.classList.add('text-center');
                            tdRentalCount.textContent = car.rentalCount;
                            tr.appendChild(tdRentalCount);

                            tableBody.appendChild(tr);
                            console.log(`DEBUG: Ligne complète ajoutée au tableau pour la voiture: ${car.immatriculation}`);
                        });
                        console.log("DEBUG: Tableau des voitures les plus louées rendu avec", cars.length, "éléments.");
                    } else {
                        tableBody.innerHTML = '<tr><td colspan="4" class="text-center text-gray-500 py-4">Aucune donnée trouvée pour les critères sélectionnés.</td></tr>';
                        console.log("DEBUG: Aucune voiture trouvée pour les critères de voitures les plus louées.");
                    }
                } catch (error) {
                    console.error('ERREUR: lors de la récupération des voitures les plus louées:', error);
                    if (carsFilterError) {
                        carsFilterError.textContent = `Erreur lors du chargement des voitures les plus louées: ${error.message}`;
                        carsFilterError.classList.remove('hidden');
                    }
                    tableBody.innerHTML = '<tr><td colspan="4" class="text-center text-gray-500 py-4">Échec du chargement des données.</td></tr>';
                }
            }

            // Écouteur d'événement pour les filtres des voitures
            const applyCarsFilterBtn = document.getElementById('applyCarsFilter');
            if (applyCarsFilterBtn) {
                applyCarsFilterBtn.addEventListener('click', fetchAndRenderMostRentedCars);
                console.log("DEBUG: Écouteur d'événement pour applyCarsFilterBtn ajouté.");
            }
            document.getElementById('topNCarsFilter')?.addEventListener('change', fetchAndRenderMostRentedCars);
            document.getElementById('periodCarsFilter')?.addEventListener('change', fetchAndRenderMostRentedCars);


            // --- Fonctions de chargement et rendu pour la section Bilan Financier ---
            async function fetchAndRenderFinancialChart() {
                console.log("DEBUG: Appel de fetchAndRenderFinancialChart()");
                const period = document.getElementById('periodFinancialChart').value;
                const financialChartCanvas = document.getElementById('financialChart');
                const financialChartError = document.getElementById('financialChartError');

                if (!financialChartCanvas) {
                    console.warn('AVERTISSEMENT: Élément canvas "financialChart" non trouvé.');
                    return;
                }

                if (financialChartInstance) {
                    financialChartInstance.destroy();
                    console.log("DEBUG: Ancien graphique financier détruit.");
                }

                financialChartError.classList.add('hidden');

                try {
                    const response = await fetch(`api/reports/financial-over-period?period=${period}`);
                    if (!response.ok) {
                        const errorData = await response.json().catch(() => ({ message: response.statusText }));
                        throw new Error(`Échec de la récupération des données du graphique financier: ${errorData.message || response.statusText}`);
                    }
                    const data = await response.json();
                    console.log("DEBUG: Données du graphique financier reçues:", data);

                    const labels = data.map(item => item.label);
                    const revenues = data.map(item => item.totalRevenue);

                    const ctx = financialChartCanvas.getContext('2d');
                    financialChartInstance = new Chart(ctx, {
                        type: 'polarArea',
                        data: {
                            labels: labels,
                            datasets: [{
                                label: 'Revenu Total (€)',
                                data: revenues,
                                backgroundColor: [
                                    'rgba(255, 99, 132, 0.7)', 'rgba(54, 162, 235, 0.7)', 'rgba(255, 206, 86, 0.7)',
                                    'rgba(75, 192, 192, 0.7)', 'rgba(153, 102, 255, 0.7)', 'rgba(255, 159, 64, 0.7)'
                                ],
                                borderColor: [
                                    'rgba(255, 99, 132, 1)', 'rgba(54, 162, 235, 1)', 'rgba(255, 206, 86, 1)',
                                    'rgba(75, 192, 192, 1)', 'rgba(153, 102, 255, 1)', 'rgba(255, 159, 64, 1)'
                                ],
                                borderWidth: 1
                            }]
                        },
                        options: {
                            responsive: true,
                            maintainAspectRatio: false,
                            plugins: {
                                title: {
                                    display: true,
                                    text: `Bilan Financier par Période (${period})`,
                                    font: { size: 16 }
                                },
                                tooltip: {
                                    callbacks: {
                                        label: function(context) {
                                            let label = context.label || '';
                                            if (label) { label += ': '; }
                                            if (context.parsed.r !== null) {
                                                label += new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(context.parsed.r);
                                            }
                                            return label;
                                        }
                                    }
                                }
                            },
                            scales: {
                                r: {
                                    pointLabels: {
                                        display: true,
                                        centerPointLabels: true,
                                        font: { size: 14 }
                                    },
                                    grid: { color: 'rgba(0, 0, 0, 0.1)' }
                                }
                            }
                        }
                    });
                    financialChartError.classList.add('hidden');
                    console.log("DEBUG: Graphique financier rendu.");

                } catch (error) {
                    console.error('ERREUR: lors de la récupération des données du graphique financier:', error);
                    if (financialChartCanvas.getContext('2d')) {
                        financialChartCanvas.getContext('2d').clearRect(0, 0, financialChartCanvas.width, financialChartCanvas.height);
                    }
                    if (financialChartError) {
                        financialChartError.textContent = `Erreur lors du chargement du graphique financier: ${error.message}`;
                        financialChartError.classList.remove('hidden');
                    }
                    if (financialChartInstance) {
                        financialChartInstance.destroy();
                        financialChartInstance = null;
                    }
                }
            }

            async function fetchAndDisplayMonthlyReport() {
                console.log("DEBUG: Appel de fetchAndDisplayMonthlyReport()");
                const monthSelect = document.getElementById('monthSelector');
                const yearSelect = document.getElementById('yearSelector');

                const month = monthSelect && monthSelect.value ? parseInt(monthSelect.value) : (new Date().getMonth() + 1);
                const year = yearSelect && yearSelect.value ? parseInt(yearSelect.value) : new Date().getFullYear();

                const monthlyRevenueDisplay = document.getElementById('monthlyRevenueDisplay');
                const monthlyReportMonthDisplay = document.getElementById('monthlyReportMonthDisplay');
                const monthlyReportYearDisplay = document.getElementById('monthlyReportYearDisplay');
                const monthlyReportError = document.getElementById('monthlyReportError');

                if (!monthlyRevenueDisplay || !monthlyReportMonthDisplay || !monthlyReportYearDisplay || !monthlyReportError) {
                    console.warn("AVERTISSEMENT: Éléments du rapport mensuel non trouvés.");
                    return;
                }

                monthlyRevenueDisplay.textContent = 'Chargement...';
                monthlyReportMonthDisplay.textContent = '...';
                monthlyReportYearDisplay.textContent = '...';
                monthlyReportError.classList.add('hidden');

                const monthNames = ["Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"];

                try {
                    if (isNaN(month) || isNaN(year)) {
                        throw new Error("Mois ou année non valides.");
                    }
                    
                    console.log('DEBUG: fetchAndDisplayMonthlyReport - Mois:', month, 'Année:', year);
                    const requestUrl = "api/reports/monthly-financial-stats?year=" + year + "&month=" + month;
                    console.log('DEBUG: URL de la requête envoyée pour le rapport mensuel:', requestUrl);

                    const response = await fetch(requestUrl);
                    if (!response.ok) {
                        const errorData = await response.json().catch(() => ({ message: response.statusText, status: response.status }));
                        throw new Error(`Échec de la récupération du bilan mensuel: ${errorData.status || response.status} - ${errorData.message || response.statusText}`);
                    }
                    const data = await response.json();
                    console.log("DEBUG: Données du rapport mensuel reçues:", data);

                    monthlyRevenueDisplay.textContent = (data.totalRevenue !== undefined && data.totalRevenue !== null)
                        ? new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(data.totalRevenue)
                        : '0,00 €';
                    monthlyReportMonthDisplay.textContent = monthNames[month - 1];
                    monthlyReportYearDisplay.textContent = year;
                    console.log("DEBUG: Rapport mensuel affiché.");

                } catch (error) {
                    console.error('ERREUR: lors de la récupération du bilan mensuel:', error);
                    monthlyRevenueDisplay.textContent = 'N/A';
                    monthlyReportMonthDisplay.textContent = monthNames[month - 1] || 'N/A';
                    monthlyReportYearDisplay.textContent = year || 'N/A';
                    if (monthlyReportError) {
                        monthlyReportError.textContent = `Erreur lors du chargement du bilan mensuel: ${error.message}`;
                        monthlyReportError.classList.remove('hidden');
                    }
                }
            }

            // Écouteurs d'événements pour les filtres du bilan financier
            document.getElementById('periodFinancialChart')?.addEventListener('change', fetchAndRenderFinancialChart);
            const applyMonthlyReportFilterButton = document.getElementById('applyMonthlyReportFilter');
            if (applyMonthlyReportFilterButton) {
                applyMonthlyReportFilterButton.addEventListener('click', function(e) {
                    e.preventDefault();
                    fetchAndDisplayMonthlyReport();
                });
                console.log("DEBUG: Écouteur d'événement pour applyMonthlyReportFilterButton ajouté.");
            }
            document.getElementById('monthSelector')?.addEventListener('change', fetchAndDisplayMonthlyReport);
            document.getElementById('yearSelector')?.addEventListener('change', fetchAndDisplayMonthlyReport);


            // --- NOUVELLES FONCTIONS POUR LA GESTION DES GESTIONNAIRES ---
            const managerFormContainer = document.getElementById('managerFormContainer');
            const toggleAddManagerFormBtn = document.getElementById('toggleAddManagerFormBtn');
            const cancelManagerFormBtn = document.getElementById('cancelManagerFormBtn');
            const managersListContainer = document.getElementById('managersListContainer');
            const managerForm = document.getElementById('managerForm');
            const managerFormTitle = document.getElementById('managerFormTitle');
            const submitManagerBtn = document.getElementById('submitManagerBtn');
            const managerFormMessage = document.getElementById('managerFormMessage');

            function showManagerForm(isEditMode = false, managerData = {}) {
                console.log("DEBUG: Appel de showManagerForm() - Mode édition:", isEditMode, "Données:", managerData);
                if (managerFormContainer && managersListContainer) {
                    managerFormContainer.classList.remove('hidden-element');
                    managersListContainer.classList.add('hidden-element');
                    if (toggleAddManagerFormBtn) toggleAddManagerFormBtn.style.display = 'none';

                    // Réinitialiser le formulaire
                    managerForm.reset();
                    managerFormMessage.textContent = '';
                    managerFormMessage.classList.remove('text-green-600', 'text-red-600', 'text-gray-600');

                    if (isEditMode) {
                        managerFormTitle.textContent = "Modifier le Gestionnaire";
                        submitManagerBtn.textContent = "Enregistrer les modifications";
                        submitManagerBtn.classList.remove('btn-primary');
                        submitManagerBtn.classList.add('action-button', 'edit');

                        // Pré-remplir le formulaire avec les données du gestionnaire
                        document.getElementById('managerId').value = managerData.id || '';
                        document.getElementById('managerUsername').value = managerData.username || '';
                        document.getElementById('managerPassword').value = ''; // Ne jamais pré-remplir le mot de passe
                        document.getElementById('managerNom').value = managerData.nom || '';
                        document.getElementById('managerPrenom').value = managerData.prenom || '';
                        if (managerData.dateRecrutement) {
                            document.getElementById('managerDateRecrutement').value = managerData.dateRecrutement;
                        } else {
                            document.getElementById('managerDateRecrutement').value = '';
                        }
                        document.getElementById('managerEmail').value = managerData.email || '';
                        document.getElementById('managerTelephone').value = managerData.telephone || '';
                        document.getElementById('managerAdresse').value = managerData.adresse || '';

                        document.getElementById('managerUsername').setAttribute('readonly', 'true');
                        document.getElementById('managerUsername').classList.add('bg-gray-100', 'cursor-not-allowed');
                        console.log("DEBUG: Formulaire en mode édition, champs pré-remplis.");

                    } else {
                        managerFormTitle.textContent = "Ajouter un Nouveau Gestionnaire";
                        submitManagerBtn.textContent = "Ajouter Gestionnaire";
                        submitManagerBtn.classList.remove('action-button', 'edit');
                        submitManagerBtn.classList.add('btn-primary');

                        document.getElementById('managerUsername').removeAttribute('readonly');
                        document.getElementById('managerUsername').classList.remove('bg-gray-100', 'cursor-not-allowed');
                        console.log("DEBUG: Formulaire en mode ajout.");
                    }
                } else {
                    console.warn("AVERTISSEMENT: Éléments du formulaire de gestionnaire non trouvés pour showManagerForm.");
                }
            }

            function hideManagerForm() {
                console.log("DEBUG: Appel de hideManagerForm()");
                if (managerFormContainer && managersListContainer) {
                    managerFormContainer.classList.add('hidden-element');
                    managersListContainer.classList.remove('hidden-element');
                    if (toggleAddManagerFormBtn) toggleAddManagerFormBtn.style.display = 'block';
                    managerForm.reset();
                    managerFormMessage.textContent = '';
                    managerFormMessage.classList.remove('text-green-600', 'text-red-600', 'text-gray-600');
                    document.getElementById('managerUsername').removeAttribute('readonly');
                    document.getElementById('managerUsername').classList.remove('bg-gray-100', 'cursor-not-allowed');
                    console.log("DEBUG: Formulaire de gestionnaire masqué.");
                } else {
                    console.warn("AVERTISSEMENT: Éléments du formulaire de gestionnaire non trouvés pour hideManagerForm.");
                }
            }

            if (toggleAddManagerFormBtn) {
                toggleAddManagerFormBtn.addEventListener('click', () => showManagerForm(false));
                console.log("DEBUG: Écouteur d'événement pour toggleAddManagerFormBtn ajouté.");
            }

            if (cancelManagerFormBtn) {
                cancelManagerFormBtn.addEventListener('click', hideManagerForm);
                console.log("DEBUG: Écouteur d'événement pour cancelManagerFormBtn ajouté.");
            }

            // Gestion de la soumission du formulaire d'ajout/modification
            if (managerForm) {
                managerForm.addEventListener('submit', async function(e) {
                    e.preventDefault();
                    console.log("DEBUG: Formulaire de gestionnaire soumis.");
                    managerFormMessage.textContent = 'Envoi...';
                    managerFormMessage.classList.remove('text-green-600', 'text-red-600');
                    managerFormMessage.classList.add('text-gray-600');

                    const formData = new FormData(managerForm);
                    const managerData = {};
                    formData.forEach((value, key) => {
                        managerData[key] = value;
                    });
                    console.log("DEBUG: Données du formulaire:", managerData);

                    const isEdit = managerData.id && managerData.id !== '';
                    const apiUrl = isEdit ? 'api/managers/update' : 'api/managers/add';
                    const method = isEdit ? 'PUT' : 'POST';
                    console.log(`DEBUG: Mode: ${isEdit ? 'Modification' : 'Ajout'}, URL: ${apiUrl}, Méthode: ${method}`);

                    if (isEdit && managerData.password === '') {
                        delete managerData.password;
                        console.log("DEBUG: Mot de passe non modifié en mode édition, supprimé des données.");
                    }

                    try {
                        const response = await fetch(apiUrl, {
                            method: method,
                            headers: { 'Content-Type': 'application/json' },
                            body: JSON.stringify(managerData)
                        });

                        const result = await response.json();
                        console.log("DEBUG: Réponse de l'API d'ajout/modification:", result);

                        if (response.ok) {
                            managerFormMessage.textContent = result.message;
                            managerFormMessage.classList.remove('text-gray-600', 'text-red-600');
                            managerFormMessage.classList.add('text-green-600');
                            hideManagerForm();
                            fetchAndRenderManagers(); // Recharge la liste des gestionnaires
                            console.log("DEBUG: Opération réussie, liste rechargée.");
                        } else {
                            managerFormMessage.textContent = result.message || `Erreur lors de ${isEdit ? 'la modification' : 'l\'ajout'} du gestionnaire.`;
                            managerFormMessage.classList.remove('text-gray-600', 'text-green-600');
                            managerFormMessage.classList.add('text-red-600');
                            console.error("ERREUR: Opération échouée:", result);
                        }
                    } catch (error) {
                        console.error(`ERREUR: lors de ${isEdit ? 'la modification' : 'l\'ajout'} du gestionnaire:`, error);
                        managerFormMessage.textContent = `Erreur réseau ou interne: ${error.message}`;
                        managerFormMessage.classList.remove('text-gray-600', 'text-green-600');
                        managerFormMessage.classList.add('text-red-600');
                    }
                });
                console.log("DEBUG: Écouteur d'événement pour managerForm submit ajouté.");
            }


            async function fetchAndRenderManagers() {
                console.log("DEBUG: Appel de fetchAndRenderManagers()");
                const managersTableBody = document.getElementById('managersTableBody');
                const listManagersError = document.getElementById('listManagersError');

                if (!managersTableBody || !managersListContainer) {
                    console.warn("AVERTISSEMENT: Éléments du tableau des gestionnaires non trouvés.");
                    return;
                }

                managersTableBody.innerHTML = '<tr><td colspan="10" class="text-center text-gray-500 py-4">Chargement des gestionnaires...</td></tr>';
                listManagersError.classList.add('hidden-element');

                try {
                    const response = await fetch('api/managers/list');
                    if (!response.ok) {
                        console.error('ERREUR: Réponse de /api/managers/list non OK. Statut:', response.status, 'Texte Statut:', response.statusText);
                        const errorData = await response.json().catch(() => ({ message: response.statusText }));
                        throw new Error(`Échec de la récupération des gestionnaires: ${errorData.message || response.statusText}`);
                    }
                    const managers = await response.json();
                    console.log('DEBUG: Données reçues pour les Gestionnaires (/api/managers/list):', managers);

                    managersTableBody.innerHTML = ''; // Clear loading message
                    if (managers.length > 0) {
                        managers.forEach(manager => {
                            console.log('DEBUG: Traitement du gestionnaire pour rendu:', manager); 

                            const tr = document.createElement('tr');
                            tr.setAttribute('data-manager-id', manager.id);

                            const tdId = document.createElement('td');
                            tdId.textContent = String(manager.id || 'N/A');
                            tr.appendChild(tdId);

                            const tdUsername = document.createElement('td');
                            tdUsername.textContent = String(manager.username || 'N/A');
                            tr.appendChild(tdUsername);

                            const tdNom = document.createElement('td');
                            tdNom.textContent = String(manager.nom || 'N/A');
                            tr.appendChild(tdNom);

                            const tdPrenom = document.createElement('td');
                            tdPrenom.textContent = String(manager.prenom || 'N/A');
                            tr.appendChild(tdPrenom);

                            const tdDateRecrutement = document.createElement('td');
                            const formattedDate = manager.dateRecrutement ? new Date(manager.dateRecrutement).toLocaleDateString('fr-FR') : 'N/A';
                            tdDateRecrutement.textContent = formattedDate;
                            tr.appendChild(tdDateRecrutement);

                            const tdEmail = document.createElement('td');
                            tdEmail.textContent = String(manager.email || 'N/A');
                            tr.appendChild(tdEmail);

                            const tdTelephone = document.createElement('td');
                            tdTelephone.textContent = String(manager.telephone || 'N/A');
                            tr.appendChild(tdTelephone);

                            const tdAdresse = document.createElement('td');
                            tdAdresse.textContent = String(manager.adresse || 'N/A');
                            tr.appendChild(tdAdresse);

                            const tdRole = document.createElement('td');
                            tdRole.textContent = String(manager.role || 'N/A');
                            tr.appendChild(tdRole);
                            
                            // Nouvelle cellule pour les actions
                            const tdActions = document.createElement('td');
                            tdActions.classList.add('whitespace-nowrap', 'text-center');

                            // Bouton Modifier
                            const editButton = document.createElement('button');
                            editButton.classList.add('action-button', 'edit', 'mr-2');
                            editButton.innerHTML = '<i class="fas fa-edit"></i> Modifier';
                            editButton.onclick = () => {
                                console.log("DEBUG: Bouton Modifier cliqué pour le gestionnaire:", manager.id);
                                editManager(manager);
                            };
                            tdActions.appendChild(editButton);

                            // Bouton Supprimer
                            const deleteButton = document.createElement('button');
                            deleteButton.classList.add('action-button', 'delete');
                            deleteButton.innerHTML = '<i class="fas fa-trash-alt"></i> Supprimer';
                            deleteButton.onclick = () => {
                                console.log("DEBUG: Bouton Supprimer cliqué pour le gestionnaire:", manager.id);
                                showDeleteConfirmModal(manager.id, manager.username);
                            };
                            tdActions.appendChild(deleteButton);

                            tr.appendChild(tdActions);
                            
                            managersTableBody.appendChild(tr);
                        });
                        console.log("DEBUG: Tableau des gestionnaires rendu avec", managers.length, "éléments.");
                    } else {
                        managersTableBody.innerHTML = '<tr><td colspan="10" class="text-center text-gray-500 py-4">Aucun gestionnaire trouvé.</td></tr>';
                        console.log("DEBUG: Aucun gestionnaire trouvé, affichage du message 'Aucun gestionnaire'.");
                    }
                } catch (error) {
                    console.error('ERREUR: lors de la récupération des gestionnaires:', error);
                    if (listManagersError) {
                        listManagersError.textContent = `Erreur lors du chargement des gestionnaires: ${error.message}`;
                        listManagersError.classList.remove('hidden-element');
                    }
                    managersTableBody.innerHTML = '<tr><td colspan="10" class="text-center text-red-500 py-4">Échec du chargement des données. Veuillez vérifier les logs du serveur.</td></tr>';
                }
            }

            // Fonction pour éditer un gestionnaire
            function editManager(manager) {
                console.log("DEBUG: Appel de editManager() pour ID:", manager.id);
                showManagerForm(true, manager);
            }

            // --- Gestion du modal de confirmation de suppression ---
            const deleteConfirmModal = document.getElementById('deleteConfirmModal');
            const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
            const cancelDeleteBtn = document.getElementById('cancelDeleteBtn');
            let managerIdToDelete = null; // Variable pour stocker l'ID du gestionnaire à supprimer

            function showDeleteConfirmModal(id, username) {
                console.log("DEBUG: Appel de showDeleteConfirmModal() pour ID:", id, "Username:", username);
                managerIdToDelete = id;
                if (deleteConfirmModal) {
                    deleteConfirmModal.querySelector('p').innerHTML = `Êtes-vous sûr de vouloir supprimer le gestionnaire <strong>${username}</strong> ? Cette action est irréversible.`;
                    deleteConfirmModal.classList.remove('hidden-element');
                    console.log("DEBUG: Modal de confirmation affiché.");
                } else {
                    console.error("ERREUR: Élément deleteConfirmModal non trouvé.");
                }
            }

            function hideDeleteConfirmModal() {
                console.log("DEBUG: Appel de hideDeleteConfirmModal()");
                if (deleteConfirmModal) {
                    deleteConfirmModal.classList.add('hidden-element');
                    managerIdToDelete = null;
                    console.log("DEBUG: Modal de confirmation masqué.");
                } else {
                    console.error("ERREUR: Élément deleteConfirmModal non trouvé pour le masquer.");
                }
            }

            if (cancelDeleteBtn) {
                cancelDeleteBtn.addEventListener('click', hideDeleteConfirmModal);
                console.log("DEBUG: Écouteur d'événement pour cancelDeleteBtn ajouté.");
            }

            if (confirmDeleteBtn) {
                confirmDeleteBtn.addEventListener('click', async () => {
                    console.log("DEBUG: Bouton Confirmer Suppression cliqué.");
                    if (managerIdToDelete) {
                        hideDeleteConfirmModal();
                        await deleteManager(managerIdToDelete);
                    } else {
                        console.warn("AVERTISSEMENT: managerIdToDelete est null lors de la confirmation de suppression.");
                    }
                });
                console.log("DEBUG: Écouteur d'événement pour confirmDeleteBtn ajouté.");
            }

            // Fonction pour supprimer un gestionnaire
            async function deleteManager(id) {
                console.log("DEBUG: Appel de deleteManager() pour ID:", id);
                const messageElement = document.getElementById('listManagersError');
                if (messageElement) messageElement.classList.add('hidden-element');

                try {
                    const response = await fetch(`api/managers/delete?id=${id}`, {
                        method: 'DELETE'
                    });

                    const result = await response.json();
                    console.log("DEBUG: Réponse de l'API de suppression:", result);

                    if (response.ok) {
                        if (messageElement) {
                            messageElement.textContent = result.message;
                            messageElement.classList.remove('text-red-600');
                            messageElement.classList.add('text-green-600');
                            messageElement.classList.remove('hidden-element');
                        }
                        fetchAndRenderManagers();
                        console.log("DEBUG: Suppression réussie, liste rechargée.");
                    } else {
                        if (messageElement) {
                            messageElement.textContent = result.message || 'Erreur lors de la suppression du gestionnaire.';
                            messageElement.classList.remove('text-green-600');
                            messageElement.classList.add('text-red-600');
                            messageElement.classList.remove('hidden-element');
                        }
                        console.error("ERREUR: Échec de la suppression:", result);
                    }
                } catch (error) {
                    console.error('ERREUR: lors de la suppression du gestionnaire:', error);
                    if (messageElement) {
                        messageElement.textContent = `Erreur réseau ou interne lors de la suppression: ${error.message}`;
                        messageElement.classList.remove('text-green-600');
                        messageElement.classList.add('text-red-600');
                        messageElement.classList.remove('hidden-element');
                    }
                }
            }


            // --- Gestion de l'affichage initial des sections ---
            const urlParams = new URLSearchParams(window.location.search);
            const initialContentTab = urlParams.get('content') || 'overviewDetails';
            console.log("DEBUG: Onglet initial à charger:", initialContentTab);

            document.querySelectorAll('.horizontal-nav a').forEach(link => {
                link.addEventListener('click', function(e) {
                    e.preventDefault();
                    const contentId = this.getAttribute('data-content-id');
                    console.log("DEBUG: Clic sur le lien de navigation:", contentId);
                    showDynamicContent(contentId);
                });
            });

            // Fonction pour afficher une section de contenu dynamique
            function showDynamicContent(contentId) {
                console.log("DEBUG: Appel de showDynamicContent() pour ID:", contentId);
                document.querySelectorAll('.dynamic-content').forEach(content => {
                    content.classList.add('hidden-element'); // Masquer tous les contenus
                    content.classList.remove('active');
                });

                const activeContent = document.getElementById(contentId);
                if (activeContent) {
                    activeContent.classList.remove('hidden-element'); // Afficher le contenu actif
                    activeContent.classList.add('active');
                    console.log("DEBUG: Contenu", contentId, "affiché.");
                } else {
                    console.warn("AVERTISSEMENT: Contenu dynamique avec ID", contentId, "non trouvé.");
                }

                document.querySelectorAll('.horizontal-nav a').forEach(link => {
                    if (link.getAttribute('data-content-id') === contentId) {
                        link.classList.add('active');
                        link.classList.add('bg-gray-200'); // Ajouter une classe pour l'état actif visuel
                    } else {
                        link.classList.remove('active');
                        link.classList.remove('bg-gray-200');
                    }
                });

                // --- Chargement des données spécifiques à chaque onglet lors de son activation ---\
                if (contentId === 'overviewDetails') {
                    fetchCarStatsAndRenderChart();
                } else if (contentId === 'carsDetails') {
                    const topNCarsFilter = document.getElementById('topNCarsFilter');
                    const periodCarsFilter = document.getElementById('periodCarsFilter');
                    if (topNCarsFilter) topNCarsFilter.value = '5';
                    if (periodCarsFilter) periodCarsFilter.value = 'all';
                    fetchAndRenderMostRentedCars();
                } else if (contentId === 'financialDetails') {
                    const periodFinancialChart = document.getElementById('periodFinancialChart');
                    if (periodFinancialChart) periodFinancialChart.value = '3months';
                    
                    const monthSelector = document.getElementById('monthSelector');
                    const yearSelector = document.getElementById('yearSelector');
                    const currentMonth = new Date().getMonth() + 1;
                    const currentYear = new Date().getFullYear();
                    
                    if (monthSelector && (monthSelector.value === "" || isNaN(parseInt(monthSelector.value)))) {
                        monthSelector.value = currentMonth;
                    }
                    if (yearSelector && (yearSelector.value === "" || isNaN(parseInt(yearSelector.value)))) {
                        yearSelector.value = currentYear;
                    }

                    fetchAndRenderFinancialChart();
                    fetchAndDisplayMonthlyReport();
                } else if (contentId === 'managersDetails') {
                    console.log("DEBUG: Onglet Gestionnaires activé. Appel de fetchAndRenderManagers().");
                    fetchAndRenderManagers();
                    // Assurez-vous que le formulaire est masqué initialement
                    hideManagerForm();
                }
            }

            // Exécute la fonction pour afficher le contenu initial et charger les données
            showDynamicContent(initialContentTab);
            console.log("DEBUG: showDynamicContent initial appelé.");

            // Rendre la fonction downloadList accessible globalement
            window.downloadList = downloadList;
        });

        // Fonction globale pour le téléchargement des listes PDF
        function downloadList(listType) {
            console.log('DEBUG: Tentative de téléchargement de la liste : ' + listType);
            const contextPath = "<%= request.getContextPath() %>";
            const exportUrl = `${contextPath}/export?type=${listType}`;
            window.open(exportUrl, '_blank');
        }
    </script>
</body>
</html>
