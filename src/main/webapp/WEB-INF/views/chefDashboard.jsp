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
    <!-- Chargement de Tailwind CSS via CDN -->
    <script src="https://cdn.tailwindcss.com"></script>
    <!-- Font Awesome pour les icônes -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <!-- Police Inter depuis Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Chart.js pour les graphiques -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <!-- Votre fichier CSS personnalisé -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
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
            </nav>

            <%-- Conteneur pour le contenu dynamique des onglets --%>
            <div id="dynamicContentContainer">

                <%-- Section: Vue d'ensemble (Dashboard Overview) --%>
                <div id="overviewDetails" class="dynamic-content">
                    <h1 class="text-3xl font-bold text-gray-800 mb-6 text-center">Vue d'ensemble de l'Agence</h1>

                    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                        <div class="md:col-span-1 lg:col-span-1 flex flex-col gap-6">
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
                                <p class="card-value text-green-600" id="availableCarsValue"> <%-- AJOUT ID --%>
                                    <c:out value="${requestScope.nombreVoituresDisponibles != null ? requestScope.nombreVoituresDisponibles : 'N/A'}"/>
                                </p>
                            </div>
                            <%-- Carte: Voitures Louées --%>
                            <div class="card">
                                <h3 class="card-title">Voitures Louées</h3>
                                <p class="card-value text-yellow-600" id="rentedCarsValue"> <%-- AJOUT ID --%>
                                    <c:out value="${requestScope.nombreVoituresLouees != null ? requestScope.nombreVoituresLouees : 'N/A'}"/>
                                </p>
                            </div>
                            <%-- Carte: Demandes en Attente --%>
                            <div class="card">
                                <h3 class="card-title">Demandes en Attente</h3>
                                <p class="card-value text-red-600" id="pendingRequestsValue"> <%-- AJOUT ID --%>
                                    <c:out value="${requestScope.pendingRequestsCount != null ? requestScope.pendingRequestsCount : 'N/A'}"/>
                                </p>
                            </div>
                        </div>

                        <div class="card md:col-span-1 lg:col-span-2 chart-container">
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
                    <div class="overflow-x-auto rounded-lg shadow mb-8"> <%-- Ajout de mb-8 pour espacement --%>
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
                                        <%-- CORRECTION ICI : entry est directement une Voiture --%>
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
                                <option value="11" <c:if test="${param.month == '11' || (empty param.month && now.monthValue == 11)}">selected</c:if>>Novembre</option> <%-- CORRECTION ICI --%>
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
        document.addEventListener('DOMContentLoaded', function() {
            let carStatusPieChartInstance = null;
            let financialChartInstance = null;

            // Fonction de simulation de fetch pour les données (À REMPLACER PAR VOS APPELS AJAX RÉELS VERS VOS SERVLETS/API)
            async function simulatedFetch(url) {
                return new Promise(resolve => {
                    setTimeout(() => {
                        // Valeurs initiales JSP pour les statistiques de voitures
                        // Assurez-vous que ces variables JSP sont correctement passées du backend à cette page
                        // Convertissez les valeurs à null ou 0 si elles sont "N/A" pour éviter NaN
                        const totalVoitures = parseInt('${requestScope.nombreTotalVoitures}') || 0;
                        const nombreVoituresDisponibles = parseInt('${requestScope.nombreVoituresDisponibles}') || 0;
                        const nombreVoituresLouees = parseInt('${requestScope.nombreVoituresLouees}') || 0;
                        const pendingRequestsCount = parseInt('${requestScope.pendingRequestsCount}') || 0;

                        const mockCarStats = { total: totalVoitures, available: nombreVoituresDisponibles, rented: nombreVoituresLouees, pending: pendingRequestsCount };

                        const mockMostRentedCars = {
                            "all": [
                                { immatriculation: "AB-123-CD", marque: "Renault", modele: "Clio", rentalCount: 25 },
                                { immatriculation: "EF-456-GH", marque: "Peugeot", modele: "308", rentalCount: 20 },
                                { immatriculation: "IJ-789-KL", marque: "BMW", modele: "X5", rentalCount: 15 },
                                { immatriculation: "MN-012-OP", marque: "Mercedes", modele: "Classe C", rentalCount: 12 },
                                { immatriculation: "QR-345-ST", marque: "Ford", modele: "Focus", rentalCount: 10 }
                            ],
                            "3months": [
                                { immatriculation: "AB-123-CD", marque: "Renault", modele: "Clio", rentalCount: 10 },
                                { immatriculation: "EF-456-GH", marque: "Peugeot", modele: "308", rentalCount: 8 },
                                { immatriculation: "IJ-789-KL", marque: "BMW", modele: "X5", rentalCount: 5 }
                            ],
                            "6months": [
                                { immatriculation: "AB-123-CD", marque: "Renault", modele: "Clio", rentalCount: 18 },
                                { immatriculation: "EF-456-GH", marque: "Peugeot", modele: "308", rentalCount: 15 },
                                { immatriculation: "IJ-789-KL", marque: "BMW", modele: "X5", rentalCount: 10 }
                            ],
                            "currentYear": [
                                { immatriculation: "AB-123-CD", marque: "Renault", modele: "Clio", rentalCount: 20 },
                                { immatriculation: "EF-456-GH", marque: "Peugeot", modele: "308", rentalCount: 17 },
                                { immatriculation: "IJ-789-KL", marque: "BMW", modele: "X5", rentalCount: 12 }
                            ]
                        };

                        // Récupérer l'année courante depuis la JSP pour les mocks de dates
                        const currentYear = parseInt('${now.year}');
                        // const currentMonthValue = parseInt('${now.monthValue}'); // Non utilisé ici, mais peut être utile

                        const mockFinancialOverPeriod = {
                            "3months": [
                                { label: `Avr ${currentYear}`, totalRevenue: 12000.50 },
                                { label: `Mai ${currentYear}`, totalRevenue: 15500.75 },
                                { label: `Juin ${currentYear}`, totalRevenue: 18000.00 }
                            ],
                            "6months": [
                                { label: `Jan ${currentYear}`, totalRevenue: 8000.00 },
                                { label: `Fév ${currentYear}`, totalRevenue: 10500.00 },
                                { label: `Mar ${currentYear}`, totalRevenue: 13000.00 },
                                { label: `Avr ${currentYear}`, totalRevenue: 12000.50 },
                                { label: `Mai ${currentYear}`, totalRevenue: 15500.75 },
                                { label: `Juin ${currentYear}`, totalRevenue: 18000.00 }
                            ],
                            "currentYear": [
                                { label: `Jan ${currentYear}`, totalRevenue: 8000.00 },
                                { label: `Fév ${currentYear}`, totalRevenue: 10500.00 },
                                { label: `Mar ${currentYear}`, totalRevenue: 13000.00 },
                                { label: `Avr ${currentYear}`, totalRevenue: 12000.50 },
                                { label: `Mai ${currentYear}`, totalRevenue: 15500.75 },
                                { label: `Juin ${currentYear}`, totalRevenue: 18000.00 }
                            ],
                            "all": [
                                { label: "Nov 2024", totalRevenue: 9500.00 },
                                { label: "Déc 2024", totalRevenue: 11000.00 },
                                { label: "Jan 2025", totalRevenue: 8000.00 },
                                { label: "Fév 2025", totalRevenue: 10500.00 },
                                { label: "Mar 2025", totalRevenue: 13000.00 },
                                { label: "Avr 2025", totalRevenue: 12000.50 },
                                { label: "Mai 2025", totalRevenue: 15500.75 },
                                { label: "Juin 2025", totalRevenue: 18000.00 }
                            ]
                        };

                        const mockMonthlyFinancialSummary = (year, month) => {
                            const revenues = {
                                2025: {
                                    1: { totalRevenue: 8000.00, totalRentals: 15 },
                                    2: { totalRevenue: 10500.00, totalRentals: 20 },
                                    3: { totalRevenue: 13000.00, totalRentals: 25 },
                                    4: { totalRevenue: 12000.50, totalRentals: 22 },
                                    5: { totalRevenue: 15500.75, totalRentals: 28 },
                                    6: { totalRevenue: 18000.00, totalRentals: 35 },
                                    // AJOUT DE DONNÉES POUR JUILLET 2025 POUR LA SIMULATION
                                    7: { totalRevenue: 19500.00, totalRentals: 40 },
                                },
                                2024: {
                                    11: { totalRevenue: 9500.00, totalRentals: 18 },
                                    12: { totalRevenue: 11000.00, totalRentals: 21 },
                                }
                            };
                            return revenues[year] && revenues[year][month] ? revenues[year][month] : { totalRevenue: 0.00, totalRentals: 0 };
                        };

                        if (url.includes('api/reports/car-stats')) {
                            resolve({ ok: true, json: async () => mockCarStats });
                        } else if (url.includes('api/reports/most-rented-cars')) {
                            const params = new URLSearchParams(url.split('?')[1]);
                            const period = params.get('period') || 'all';
                            const limit = parseInt(params.get('limit')) || 5;
                            const data = mockMostRentedCars[period] || [];
                            resolve({ ok: true, json: async () => data.slice(0, limit) });
                        } else if (url.includes('api/reports/financial-over-period')) {
                            const params = new URLSearchParams(url.split('?')[1]);
                            const period = params.get('period') || '3months';
                            const data = mockFinancialOverPeriod[period] || [];
                            resolve({ ok: true, json: async () => data });
                        } else if (url.includes('api/locations/stats/monthly')) {
                            const params = new URLSearchParams(url.split('?')[1]);
                            const year = parseInt(params.get('year'));
                            const month = parseInt(params.get('month'));
                            resolve({ ok: true, json: async () => mockMonthlyFinancialSummary(year, month) });
                        } else {
                            resolve({ ok: false, status: 404, statusText: 'Not Found' });
                        }
                    }, 500); // Simulate network delay
                });
            }

            // Remplace la fonction fetch globale pour cette démo (RETIRER EN PRODUCTION)
            window.fetch = simulatedFetch;


            // Fonction pour afficher une section de contenu dynamique
            function showDynamicContent(contentId) {
                // Masquer toutes les sections de contenu dynamique
                document.querySelectorAll('.dynamic-content').forEach(content => {
                    content.classList.remove('active');
                    content.style.display = 'none'; // S'assurer qu'elles sont masquées
                });

                // Afficher la section de contenu dynamique demandée
                const activeContent = document.getElementById(contentId);
                if (activeContent) {
                    activeContent.classList.add('active');
                    activeContent.style.display = 'block'; // S'assurer qu'elle est visible
                }

                // Mettre à jour la classe active pour les liens de navigation horizontale
                document.querySelectorAll('.horizontal-nav a').forEach(link => {
                    if (link.getAttribute('data-content-id') === contentId) {
                        link.classList.add('active');
                    } else {
                        link.classList.remove('active');
                    }
                });

                // Gérer les chargements spécifiques de données pour chaque section
                if (contentId === 'overviewDetails') {
                    fetchCarStatsAndRenderChart();
                } else if (contentId === 'carsDetails') {
                    // S'assurer que les valeurs par défaut sont définies avant l'appel
                    document.getElementById('topNCarsFilter').value = '5';
                    document.getElementById('periodCarsFilter').value = 'all';
                    fetchAndRenderMostRentedCars();
                } else if (contentId === 'financialDetails') {
                    // S'assurer que les valeurs par défaut sont définies avant l'appel
                    document.getElementById('periodFinancialChart').value = '3months';
                    const currentMonth = new Date().getMonth() + 1;
                    const currentYear = new Date().getFullYear();
                    document.getElementById('monthSelector').value = currentMonth;
                    document.getElementById('yearSelector').value = currentYear;
                    fetchAndRenderFinancialChart();
                    fetchAndDisplayMonthlyReport(); // Appel renommé pour la clarté
                }
            }

            // --- Fonctions de chargement et rendu pour la section Vue d'ensemble ---
            async function fetchCarStatsAndRenderChart() {
                const pieChartError = document.getElementById('pieChartError');
                try {
                    const response = await fetch('api/reports/car-stats');
                    if (!response.ok) {
                        throw new Error(`Échec de la récupération des statistiques des voitures: ${response.statusText}`);
                    }
                    const stats = await response.json();

                    // Mise à jour des valeurs dans les cartes
                    document.getElementById('totalCarsValue').textContent = stats.total;
                    document.getElementById('availableCarsValue').textContent = stats.available;
                    document.getElementById('rentedCarsValue').textContent = stats.rented;
                    document.getElementById('pendingRequestsValue').textContent = stats.pending;

                    // Passer les valeurs numériques au graphique
                    renderCarStatusPieChart(stats.available, stats.rented, stats.pending);
                    pieChartError.classList.add('hidden'); // Masquer l'erreur si tout va bien
                } catch (error) {
                    console.error('Erreur lors de la récupération des statistiques des voitures:', error);
                    if (pieChartError) {
                        pieChartError.textContent = 'Erreur lors du chargement des statistiques des voitures.';
                        pieChartError.classList.remove('hidden');
                    }
                    // Détruire le graphique existant pour éviter les erreurs si les données sont invalides
                    if (carStatusPieChartInstance) {
                        carStatusPieChartInstance.destroy();
                        carStatusPieChartInstance = null;
                    }
                }
            }

            // Mise à jour de la fonction pour inclure les demandes en attente et les nouvelles couleurs
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

                carStatusPieChartInstance = new Chart(ctx.getContext('2d'), {
                    type: 'pie',
                    data: {
                        labels: ['Disponibles', 'Louées', 'En Attente'],
                        datasets: [{
                            data: [available, rented, pending],
                            backgroundColor: [
                                'rgba(34, 197, 94, 0.8)',  // Vert pour disponibles (green-500)
                                'rgba(234, 179, 8, 1)', // Jaune pour louées (yellow-400)
                                'rgba(239, 68, 68, 0.8)'   // Rouge pour en attente (red-500)
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
                    const response = await fetch(`api/reports/most-rented-cars?limit=${topN}&period=${period}`);
                    if (!response.ok) {
                        throw new Error(`Échec de la récupération des voitures les plus louées: ${response.statusText}`);
                    }
                    const cars = await response.json();

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
                        carsFilterError.textContent = 'Erreur lors du chargement des voitures les plus louées.';
                        carsFilterError.classList.remove('hidden');
                    }
                    tableBody.innerHTML = '<tr><td colspan="4" class="text-center text-gray-500 py-4">Échec du chargement des données.</td></tr>';
                }
            }

            // Écouteur d'événement pour les filtres des voitures
            const applyCarsFilterButton = document.getElementById('applyCarsFilter');
            if (applyCarsFilterButton) {
                applyCarsFilterButton.addEventListener('click', fetchAndRenderMostRentedCars);
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
                        throw new Error(`Échec de la récupération des données du graphique financier: ${response.statusText}`);
                    }
                    const data = await response.json();

                    const labels = data.map(item => item.label);
                    const revenues = data.map(item => item.totalRevenue);

                    const ctx = financialChartCanvas.getContext('2d');
                    // Utilisation d'un Polar Area Chart pour un rendu "extraordinaire et jolie"
                    financialChartInstance = new Chart(ctx, {
                        type: 'polarArea', // Changé en polarArea comme demandé
                        data: {
                            labels: labels,
                            datasets: [{
                                label: 'Revenu Total (€)',
                                data: revenues,
                                backgroundColor: [
                                    'rgba(255, 99, 132, 0.7)', // Rouge
                                    'rgba(54, 162, 235, 0.7)', // Bleu
                                    'rgba(255, 206, 86, 0.7)', // Jaune
                                    'rgba(75, 192, 192, 0.7)', // Vert
                                    'rgba(153, 102, 255, 0.7)',// Violet
                                    'rgba(255, 159, 64, 0.7)'  // Orange
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
                                            if (context.parsed.r !== null) { // 'r' est le rayon dans polarArea
                                                label += context.parsed.r.toFixed(2) + ' €';
                                            }
                                            return label;
                                        }
                                    }
                                }
                            },
                            scales: {
                                r: { // Configuration de l'échelle radiale
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
                    financialChartError.classList.add('hidden'); // Masquer l'erreur si tout va bien

                } catch (error) {
                    console.error('Erreur lors de la récupération des données du graphique financier:', error);
                    if (financialChartCanvas.getContext('2d')) {
                        financialChartCanvas.getContext('2d').clearRect(0, 0, financialChartCanvas.width, financialChartCanvas.height);
                    }
                    if (financialChartError) {
                        financialChartError.textContent = 'Erreur lors du chargement du graphique financier.';
                        financialChartError.classList.remove('hidden');
                    }
                    // Détruire le graphique existant pour éviter les erreurs si les données sont invalides
                    if (financialChartInstance) {
                        financialChartInstance.destroy();
                        financialChartInstance = null;
                    }
                }
            }

            async function fetchAndDisplayMonthlyReport() { // Renommé pour la clarté
                const monthSelect = document.getElementById('monthSelector');
                const yearSelect = document.getElementById('yearSelector');

                const month = monthSelect ? parseInt(monthSelect.value) : (new Date().getMonth() + 1);
                const year = yearSelect ? parseInt(yearSelect.value) : new Date().getFullYear();

                const monthlyRevenueDisplay = document.getElementById('monthlyRevenueDisplay');
                const monthlyReportMonthDisplay = document.getElementById('monthlyReportMonthDisplay');
                const monthlyReportYearDisplay = document.getElementById('monthlyReportYearDisplay');
                const monthlyReportError = document.getElementById('monthlyReportError');

                if (!monthlyRevenueDisplay || !monthlyReportMonthDisplay || !monthlyReportYearDisplay || !monthlyReportError) return;

                monthlyRevenueDisplay.textContent = 'Chargement...';
                monthlyReportMonthDisplay.textContent = '';
                monthlyReportYearDisplay.textContent = '';
                monthlyReportError.classList.add('hidden');

                const monthNames = ["Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"];

                try {
                    const response = await fetch(`api/locations/stats/monthly?year=${year}&month=${month}`);
                    if (!response.ok) {
                        throw new Error(`Échec de la récupération du bilan mensuel: ${response.statusText}`);
                    }
                    const data = await response.json();

                    // S'assurer que les données ne sont pas nulles avant d'appeler toFixed
                    monthlyRevenueDisplay.textContent = (data.totalRevenue !== undefined && data.totalRevenue !== null)
                        ? new Intl.NumberFormat('fr-FR', { style: 'currency', currency: 'EUR' }).format(data.totalRevenue)
                        : 'N/A';
                    monthlyReportMonthDisplay.textContent = monthNames[month - 1];
                    monthlyReportYearDisplay.textContent = year;

                } catch (error) {
                    console.error('Erreur lors de la récupération du bilan mensuel:', error);
                    monthlyRevenueDisplay.textContent = 'N/A';
                    monthlyReportMonthDisplay.textContent = monthNames[month - 1];
                    monthlyReportYearDisplay.textContent = year;
                    if (monthlyReportError) {
                        monthlyReportError.textContent = 'Erreur lors du chargement du bilan mensuel.';
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
            // Lit le paramètre 'content' de l'URL pour déterminer quelle section afficher
            const urlParams = new URLSearchParams(window.location.search);
            const initialContentTab = urlParams.get('content') || 'overviewDetails'; // 'overviewDetails' par défaut

            // Attacher les écouteurs d'événements aux liens de la barre de navigation horizontale
            document.querySelectorAll('.horizontal-nav a').forEach(link => {
                link.addEventListener('click', function(e) {
                    e.preventDefault();
                    const contentId = this.getAttribute('data-content-id');
                    showDynamicContent(contentId);
                });
            });

            // Appelle la fonction pour afficher la section et charger ses données
            showDynamicContent(initialContentTab);

            // Rendre la fonction downloadList disponible globalement pour onclick
            window.downloadList = downloadList;
        });

        // Fonction pour gérer le téléchargement (déplacée en dehors de DOMContentLoaded pour être globale)
        function downloadList(listType) {
            console.log('Tentative de téléchargement de la liste : ' + listType);
            const contextPath = "<%= request.getContextPath() %>";
            const exportUrl = `${contextPath}/export?type=${listType}`;
            window.location.href = exportUrl;
        }
    </script>
</body>
</html>
