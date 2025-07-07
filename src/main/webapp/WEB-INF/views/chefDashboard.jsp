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
</head>
<body>
    <%-- Inclusion de la barre de navigation existante (navbar.jsp) --%>
    <jsp:include page="navbar.jsp"/>

    <%-- Contenu principal de l'application --%>
    <div class="content-area" id="contentArea">
        <main class="main-content-card">
            <%-- TEST DE STYLE : Ce div devrait avoir un fond rouge et du texte blanc --%>
            <div class="bg-red-500 text-white p-4 rounded-lg mb-4 text-center">
                Ceci est un test de style. Si vous le voyez en rouge, le CSS fonctionne.
            </div>

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
                    </div>

                    <%-- NOUVELLE SECTION: Voitures Disponibles dans le Parc (ajoutée ici) --%>
                    <div class="flex items-center justify-between mt-8 mb-4">
                        <h2 class="text-2xl font-semibold text-gray-700">Voitures Disponibles dans le Parc</h2>
                        <button onclick="downloadList('available')" title="Télécharger la liste des voitures disponibles"
                                class="download-icon-button">
                            <i class="fas fa-download fa-lg"></i>
                        </button>
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
                        <button onclick="downloadList('rented')" title="Télécharger la liste des voitures louées"
                                class="download-icon-button">
                            <i class="fas fa-download fa-lg"></i>
                        </button>
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
                                                <td>${location.voiture.marque}</td>
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
                    <h1 class="text-3xl font-bold text-gray-800 mb-6 text-center">Parking des Voitures</h1>
                    <p class="mb-4 text-gray-600">Voici la liste des voitures actuellement disponibles dans notre parc.</p>

                    <div class="flex items-center justify-between mt-8 mb-4">
                        <h2 class="text-2xl font-semibold text-gray-700">Voitures Disponibles pour Location</h2>
                        <button onclick="downloadList('available')" title="Télécharger la liste des voitures disponibles"
                                class="download-icon-button">
                            <i class="fas fa-download fa-lg"></i>
                        </button>
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

            </div>
        </main>
    </div>

   <script>
        // DEBUG: Confirme que le script est chargé et exécuté
        console.log("DEBUG: Script dashboard.jsp chargé et exécuté.");

        // Déclarations globales pour les instances de graphique
        let carStatusPieChartInstance = null;
        let financialChartInstance = null;

        document.addEventListener('DOMContentLoaded', function() {
            // Initialisation des sélecteurs mois/année au mois et année actuels (si non déjà définis par JSTL)
            const monthSelector = document.getElementById('monthSelector');
            const yearSelector = document.getElementById('yearSelector');
            const currentMonth = new Date().getMonth() + 1; // getMonth() est basé sur 0
            const currentYear = new Date().getFullYear();

            // Assurez-vous que les sélecteurs ont une valeur initiale correcte
            if (monthSelector && monthSelector.value === "") { // Si JSTL n'a pas mis de valeur
                monthSelector.value = currentMonth;
            }
            if (yearSelector && yearSelector.value === "") { // Si JSTL n'a pas mis de valeur
                 yearSelector.value = currentYear;
            }


            // --- Fonctions de chargement et rendu pour la section Vue d'ensemble ---
            async function fetchCarStatsAndRenderChart() {
                const pieChartError = document.getElementById('pieChartError');
                try {
                    const response = await fetch('api/reports/car-stats');
                    if (!response.ok) {
                        const errorData = await response.json().catch(() => ({ message: response.statusText }));
                        throw new Error(`Échec de la récupération des statistiques des voitures: ${errorData.message || response.statusText}`);
                    }
                    const stats = await response.json();

                    document.getElementById('totalCarsValue').textContent = stats.totalCars || 0;
                    document.getElementById('availableCarsValue').textContent = stats.availableCars || 0;
                    document.getElementById('rentedCarsValue').textContent = stats.rentedCars || 0;
                    document.getElementById('pendingRequestsValue').textContent = stats.pendingRequests || 0;

                    renderCarStatusPieChart(stats.availableCars || 0, stats.rentedCars || 0, stats.pendingRequests || 0);
                    pieChartError.classList.add('hidden');
                } catch (error) {
                    console.error('Erreur lors de la récupération des statistiques des voitures:', error);
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
                const ctx = document.getElementById('carStatusPieChart');
                if (!ctx) {
                    console.warn('Élément canvas "carStatusPieChart" non trouvé.');
                    return;
                }

                if (carStatusPieChartInstance) {
                    carStatusPieChartInstance.destroy();
                }

                const totalForChart = available + rented + pending;
                // Si toutes les données sont zéro, Chart.js peut ne pas rendre correctement.
                // Fournir une donnée minimale ou éviter de rendre le graphique.
                if (totalForChart === 0) {
                     // Effacer le canvas si toutes les valeurs sont zéro
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
            }

            // --- Fonctions de chargement et rendu pour la section Voitures les plus louées ---
            async function fetchAndRenderMostRentedCars() {
                const topN = document.getElementById('topNCarsFilter').value;
                const period = document.getElementById('periodCarsFilter').value;
                const tableBody = document.getElementById('mostRentedCarsTableBody');
                const carsFilterError = document.getElementById('carsFilterError');

                if (!tableBody) return;

                tableBody.innerHTML = '<tr><td colspan="4" class="text-center text-gray-500 py-4">Chargement des données...</td></tr>';
                carsFilterError.classList.add('hidden');

                try {
                    // Utilisation de URLSearchParams pour une gestion robuste des paramètres
                    const params = new URLSearchParams();
                    params.append('limit', topN);
                    params.append('period', period);
                    const requestUrl = "api/reports/most-rented-cars?" + params.toString();

                    console.log('DEBUG (Avant construction URL Most Rented Cars): topN=' + topN + ', period=' + period);
                    console.log('DEBUG: URL de la requête envoyée pour Most Rented Cars (URLSearchParams):', requestUrl);

                    const response = await fetch(requestUrl);
                    if (!response.ok) {
                        const errorData = await response.json().catch(() => ({ message: response.statusText }));
                        throw new Error(`Échec de la récupération des voitures les plus louées: ${errorData.message || response.statusText}`);
                    }
                    const cars = await response.json();
                    console.log('DEBUG: Données reçues pour Most Rented Cars:', cars); // Log des données reçues

                    tableBody.innerHTML = '';
                    if (cars.length > 0) {
                        cars.forEach(car => {
                            const row = `
                                <tr>
                                    <td class="whitespace-nowrap">${car.immatriculation}</td>
                                    <td>${car.marque}</td>
                                    <td>${car.modele}</td>
                                    <td class="text-center">${car.rentalCount}</td>
                                </tr>
                            `;
                            tableBody.insertAdjacentHTML('beforeend', row);
                        });
                    } else {
                        tableBody.innerHTML = '<tr><td colspan="4" class="text-center text-gray-500 py-4">Aucune donnée trouvée pour les critères sélectionnés.</td></tr>';
                    }
                } catch (error) {
                    console.error('Erreur lors de la récupération des voitures les plus louées:', error);
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
            }
            document.getElementById('topNCarsFilter')?.addEventListener('change', fetchAndRenderMostRentedCars);
            document.getElementById('periodCarsFilter')?.addEventListener('change', fetchAndRenderMostRentedCars);


            // --- Fonctions de chargement et rendu pour la section Bilan Financier ---
            async function fetchAndRenderFinancialChart() {
                const period = document.getElementById('periodFinancialChart').value;
                const financialChartCanvas = document.getElementById('financialChart');
                const financialChartError = document.getElementById('financialChartError');

                if (!financialChartCanvas) return;

                if (financialChartInstance) {
                    financialChartInstance.destroy();
                }

                financialChartError.classList.add('hidden');

                try {
                    const response = await fetch(`api/reports/financial-over-period?period=${period}`);
                    if (!response.ok) {
                        const errorData = await response.json().catch(() => ({ message: response.statusText }));
                        throw new Error(`Échec de la récupération des données du graphique financier: ${errorData.message || response.statusText}`);
                    }
                    const data = await response.json();

                    const labels = data.map(item => item.label);
                    const revenues = data.map(item => item.totalRevenue);

                    const ctx = financialChartCanvas.getContext('2d');
                    financialChartInstance = new Chart(ctx, {
                        type: 'polarArea', // Tel que défini dans votre code
                        data: {
                            labels: labels,
                            datasets: [{
                                label: 'Revenu Total (€)',
                                data: revenues,
                                backgroundColor: [
                                    'rgba(255, 99, 132, 0.7)',
                                    'rgba(54, 162, 235, 0.7)',
                                    'rgba(255, 206, 86, 0.7)',
                                    'rgba(75, 192, 192, 0.7)',
                                    'rgba(153, 102, 255, 0.7)',
                                    'rgba(255, 159, 64, 0.7)'
                                ],
                                borderColor: [
                                    'rgba(255, 99, 132, 1)',
                                    'rgba(54, 162, 235, 1)',
                                    'rgba(255, 206, 86, 1)',
                                    'rgba(75, 192, 192, 1)',
                                    'rgba(153, 102, 255, 1)',
                                    'rgba(255, 159, 64, 1)'
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
                                            // Utilisation de parsed.r pour polarArea, et formatage monétaire
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
                                        font: {
                                            size: 14
                                        }
                                    },
                                    grid: {
                                        color: 'rgba(0, 0, 0, 0.1)'
                                    }
                                }
                            }
                        }
                    });
                    financialChartError.classList.add('hidden');

                } catch (error) {
                    console.error('Erreur lors de la récupération des données du graphique financier:', error);
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
                const monthSelect = document.getElementById('monthSelector');
                const yearSelect = document.getElementById('yearSelector');

                const month = monthSelect && monthSelect.value ? parseInt(monthSelect.value) : (new Date().getMonth() + 1);
                const year = yearSelect && yearSelect.value ? parseInt(yearSelect.value) : new Date().getFullYear();

                const monthlyRevenueDisplay = document.getElementById('monthlyRevenueDisplay');
                const monthlyReportMonthDisplay = document.getElementById('monthlyReportMonthDisplay');
                const monthlyReportYearDisplay = document.getElementById('monthlyReportYearDisplay');
                const monthlyReportError = document.getElementById('monthlyReportError');

                if (!monthlyRevenueDisplay || !monthlyReportMonthDisplay || !monthlyReportYearDisplay || !monthlyReportError) return;

                monthlyRevenueDisplay.textContent = 'Chargement...';
                monthlyReportMonthDisplay.textContent = '...';
                monthlyReportYearDisplay.textContent = '...';
                monthlyReportError.classList.add('hidden');

                const monthNames = ["Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"];

                try {
                    if (isNaN(month) || isNaN(year)) {
                        throw new Error("Mois ou année non valides.");
                    }
                    
                    // NOUVEAU LOG : Vérifie les valeurs juste avant l'interpolation
                    console.log('DEBUG (Avant interpolation): month=' + month + ', year=' + year);

                    // *** CORRECTION ICI : Utilisation de la concaténation simple au lieu des template literals ***
                    const requestUrl = "api/reports/monthly-financial-stats?year=" + year + "&month=" + month;
                    console.log('DEBUG: fetchAndDisplayMonthlyReport - Mois:', month, 'Année:', year);
                    console.log('DEBUG: URL de la requête envoyée:', requestUrl); // Nouveau log pour la chaîne finale

                    const response = await fetch(requestUrl);
                    if (!response.ok) {
                        const errorData = await response.json().catch(() => ({ message: response.statusText, status: response.status }));
                        throw new Error(`Échec de la récupération du bilan mensuel: ${errorData.status || response.status} - ${errorData.message || response.statusText}`);
                    }
                    const data = await response.json();

                    monthlyRevenueDisplay.textContent = (data.totalRevenue !== undefined && data.totalRevenue !== null)
                        ? new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(data.totalRevenue)
                        : '0,00 €';
                    monthlyReportMonthDisplay.textContent = monthNames[month - 1];
                    monthlyReportYearDisplay.textContent = year;

                } catch (error) {
                    console.error('Erreur lors de la récupération du bilan mensuel:', error);
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
            }

            document.getElementById('monthSelector')?.addEventListener('change', fetchAndDisplayMonthlyReport);
            document.getElementById('yearSelector')?.addEventListener('change', fetchAndDisplayMonthlyReport);


            // --- Gestion de l'affichage initial des sections ---
            const urlParams = new URLSearchParams(window.location.search);
            const initialContentTab = urlParams.get('content') || 'overviewDetails';

            document.querySelectorAll('.horizontal-nav a').forEach(link => {
                link.addEventListener('click', function(e) {
                    e.preventDefault();
                    const contentId = this.getAttribute('data-content-id');
                    showDynamicContent(contentId);
                });
            });

            // Fonction pour afficher une section de contenu dynamique
            function showDynamicContent(contentId) {
                document.querySelectorAll('.dynamic-content').forEach(content => {
                    content.classList.remove('active');
                    content.style.display = 'none';
                });

                const activeContent = document.getElementById(contentId);
                if (activeContent) {
                    activeContent.classList.add('active');
                    activeContent.style.display = 'block';
                }

                document.querySelectorAll('.horizontal-nav a').forEach(link => {
                    if (link.getAttribute('data-content-id') === contentId) {
                        link.classList.add('active');
                    } else {
                        link.classList.remove('active');
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
                }
            }

            // Exécute la fonction pour afficher le contenu initial et charger les données
            showDynamicContent(initialContentTab);

            // Rendre la fonction downloadList accessible globalement
            window.downloadList = downloadList;
        });

        // Fonction globale pour le téléchargement des listes PDF
        function downloadList(listType) {
            console.log('Tentative de téléchargement de la liste : ' + listType);
            const contextPath = "<%= request.getContextPath() %>";
            const exportUrl = `${contextPath}/export?type=${listType}`;
            window.open(exportUrl, '_blank');
        }
    </script>
</body>
</html>
