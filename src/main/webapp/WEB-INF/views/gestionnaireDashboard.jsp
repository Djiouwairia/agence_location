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
    <title>Tableau de Bord Gestionnaire</title>
    <!-- Chargement de Tailwind CSS via CDN -->
    <script src="https://cdn.tailwindcss.com"></script>
    <!-- Font Awesome pour les icônes -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <!-- Police Inter depuis Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Chart.js pour les graphiques -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <!-- Votre fichier CSS personnalisé (DOIT contenir tous les styles fournis précédemment) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <%-- Inclusion de la barre de navigation existante (navbar.jsp) --%>
    <jsp:include page="navbar.jsp"/>

    <%-- Contenu principal de l'application --%>
    <%-- La classe "no-sidebar" ajuste la marge gauche car il n'y a pas de sidebar fixe à gauche --%>
    <div class="content-area no-sidebar" id="contentArea">
        <main class="main-content-card">

            <%-- Titre principal du Tableau de Bord Gestionnaire - CENTRÉ --%>
            <h1 class="text-3xl font-bold text-gray-800 mb-6 text-center">Vue d'ensemble de l'Agence</h1>

            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                <div class="card">
                    <div class="card-title">Total Voitures</div>
                    <div class="card-value" id="totalCarsValue">${totalVoitures}</div>
                </div>
                <%-- Graphique circulaire pour la proportion des voitures --%>
                <div class="card md:col-span-2 lg:col-span-2 chart-container">
                     <canvas id="carStatusPieChart"></canvas>
                     <p class="js-error-message hidden" id="pieChartError"></p>
                </div>
            </div>

            <%-- Section: Voitures actuellement louées --%>
            <div class="flex items-center justify-between mt-8 mb-4"> <%-- Conteneur Flexbox pour titre et icône --%>
                <h2 class="text-2xl font-semibold text-gray-700">Voitures Actuellement Louées</h2>
                <button onclick="downloadList('rented')" title="Télécharger la liste des voitures louées"
                        class="download-icon-button"> <%-- Classe CSS spécifique --%>
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
                            <th>Locataire</th>
                            <th>CIN Locataire</th>
                            <th>Début Location</th>
                            <th>Retour Prévu</th>
                            <th class="text-center">Actions</th>
                        </tr>
                    </thead>
                    <tbody class="text-gray-700 text-sm">
                        <c:choose>
                            <c:when test="${not empty voituresLoueesAvecInfosLocataires}">
                                <c:forEach var="location" items="${voituresLoueesAvecInfosLocataires}">
                                    <tr>
                                        <td class="whitespace-nowrap">${location.voiture.immatriculation}</td>
                                        <td>${location.voiture.marque}</td>
                                        <td>${location.voiture.modele}</td>
                                        <td>${location.client.prenom} ${location.client.nom}</td>
                                        <td>${location.client.cin}</td>
                                        <td><fmt:formatDate value="${location.legacyDateDebut}" pattern="dd/MM/yyyy"/></td>
                                        <td><fmt:formatDate value="${location.legacyDateRetourPrevue}" pattern="dd/MM/yyyy"/></td>
                                        <td class="text-center">
                                            <a href="${pageContext.request.contextPath}/locations?action=return&id=${location.id}" class="text-indigo-600 hover:text-indigo-900 font-bold py-1 px-3 rounded-full bg-indigo-100 hover:bg-indigo-200 transition duration-150 ease-in-out">Retour</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="8" class="text-center text-gray-500 py-4">Aucune voiture actuellement louée.</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

            <%-- Section: Voitures Disponibles dans le Parc --%>
            <div class="flex items-center justify-between mt-8 mb-4"> <%-- Conteneur Flexbox pour titre et icône --%>
                <h2 class="text-2xl font-semibold text-gray-700">Voitures Disponibles dans le Parc</h2>
                <button onclick="downloadList('available')" title="Télécharger la liste des voitures disponibles"
                        class="download-icon-button"> <%-- Classe CSS spécifique --%>
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
                            <c:when test="${not empty voituresDisponiblesDansParking}">
                                <c:forEach var="voiture" items="${voituresDisponiblesDansParking}">
                                    <tr>
                                        <td class="whitespace-nowrap">${voiture.immatriculation}</td>
                                        <td>${voiture.marque}</td>
                                        <td>${voiture.modele}</td>
                                        <td>${voiture.statut}</td>
                                        <td>${voiture.categorie}</td>
                                        <td class="text-center">${voiture.prixLocationJ} €</td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="6" class="text-center text-gray-500 py-4">Aucune voiture disponible dans le parc.</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

        </main>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            let carStatusPieChartInstance = null;

            // --- Fonctions de simulation (À REMPLACER EN PRODUCTION) ---
            async function simulatedFetch(url) {
                return new Promise(resolve => {
                    setTimeout(() => {
                        const mockCarStats = {
                            total: ${totalVoitures},
                            available: ${nombreVoituresDisponibles},
                            rented: ${nombreVoituresLouees}
                        };
                        if (url.includes('api/reports/car-stats')) {
                            resolve({ ok: true, json: async () => mockCarStats });
                        } else {
                            resolve({ ok: false, status: 404, statusText: 'Not Found (Mock)' });
                        }
                    }, 300);
                });
            }
            window.fetch = simulatedFetch; // À RETIRER en production


            // --- Fonctions du Graphique Circulaire ---
            async function fetchCarStatsAndRenderChart() {
                try {
                    const response = await fetch('api/reports/car-stats');
                    if (!response.ok) {
                        throw new Error(`Échec de la récupération des statistiques des voitures: ${response.statusText}`);
                    }
                    const stats = await response.json();
                    document.getElementById('totalCarsValue').textContent = stats.total; // Mettre à jour la carte "Total Voitures"
                    renderCarStatusPieChart(stats.available, stats.rented);
                } catch (error) {
                    console.error('Erreur lors de la récupération des statistiques des voitures:', error);
                    const errorElement = document.getElementById('pieChartError');
                    if (errorElement) {
                        errorElement.textContent = 'Erreur lors du chargement des statistiques des voitures.';
                        errorElement.classList.remove('hidden');
                    }
                }
            }

            function renderCarStatusPieChart(available, rented) {
                const ctx = document.getElementById('carStatusPieChart');
                if (!ctx) return; // Retourne si le canvas n'est pas trouvé

                if (carStatusPieChartInstance) {
                    carStatusPieChartInstance.destroy(); // Détruire l'ancienne instance si elle existe
                }

                carStatusPieChartInstance = new Chart(ctx.getContext('2d'), {
                    type: 'pie',
                    data: {
                        labels: ['Disponibles', 'Louées'],
                        datasets: [{
                            data: [available, rented],
                            backgroundColor: [
                                'rgba(75, 192, 192, 0.8)',
                                'rgba(255, 159, 64, 0.8)'
                            ],
                            borderColor: [
                                'rgba(75, 192, 192, 1)',
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
                                text: 'Proportion des Voitures (Disponibles vs Louées)', // Ce titre est affiché sur le graphique
                                font: { size: 16 }
                            },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        let label = context.label || '';
                                        if (label) { label += ': '; }
                                        if (context.parsed !== null) {
                                            label += context.parsed + ' voitures (' + ((context.parsed / (available + rented)) * 100).toFixed(1) + '%)';
                                        }
                                        return label;
                                    }
                                }
                            }
                        }
                    }
                });
            }

            // --- Nouvelle fonction pour gérer le téléchargement ---
            function downloadList(listType) {
                console.log('Tentative de téléchargement de la liste : ' + listType);
                // Construire l'URL de la servlet d'exportation
                // Vous devrez créer une Servlet 'ExportServlet' ou similaire qui gérera cela
                const exportUrl = `${pageContext.request.contextPath}/export?type=${listType}`;
                window.location.href = exportUrl; // Cela déclenchera le téléchargement
            }

            // Appeler la fonction de chargement du graphique au démarrage
            fetchCarStatsAndRenderChart();

            // Rendre la fonction downloadList disponible globalement pour onclick
            window.downloadList = downloadList;
        });
    </script>
</body>
</html>
