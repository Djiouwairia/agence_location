<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tableau de Bord Gestionnaire</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <!-- Font Awesome pour les icônes -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <!-- Chart.js pour les diagrammes -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        /* Styles spécifiques au tableau de bord gestionnaire */
        .card {
            background-color: #ffffff;
            border-radius: 12px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
            padding: 1.5rem;
            margin-bottom: 1.5rem;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            height: 100%; /* S'assure que les cartes dans une grille sont de même hauteur */
        }
        .card-header {
            font-size: 1.1rem;
            font-weight: 600;
            color: #4a5568;
            margin-bottom: 1rem;
            border-bottom: 1px solid #edf2f7;
            padding-bottom: 0.8rem;
        }
        .card-body {
            font-size: 0.95rem;
            color: #2d3748;
        }
        .card-metric {
            font-size: 2.2rem;
            font-weight: 700;
            color: #2c5282;
            margin-top: 0.5rem;
        }
        .grid-3-cols {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2rem;
        }
        .section-title {
            font-size: 1.8rem;
            color: #2d3748;
            margin-top: 2rem;
            margin-bottom: 1.5rem;
            padding-bottom: 0.5rem;
            border-bottom: 2px solid #a0aec0;
        }
        .table-responsive {
            overflow-x: auto;
        }
        .data-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
        }
        .data-table th, .data-table td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #e2e8f0;
        }
        .data-table th {
            background-color: #ebf4ff;
            color: #2c5282;
            font-weight: 600;
            text-transform: uppercase;
            font-size: 0.85rem;
        }
        .data-table tbody tr:hover {
            background-color: #f7fafc;
        }
        .data-table td img {
            width: 80px;
            height: auto;
            border-radius: 4px;
        }
        .badge-status {
            display: inline-block;
            padding: 0.3em 0.7em;
            border-radius: 0.5em;
            font-size: 0.75em;
            font-weight: bold;
            text-align: center;
            white-space: nowrap;
        }
        .badge-status.yellow { background-color: #fffac8; color: #8a6000; } /* En attente */
        .badge-status.blue { background-color: #d1e9ff; color: #0056b3; } /* En cours */
        .badge-status.green-status { background-color: #d9f7be; color: #276749; } /* Terminee */
        .badge-status.red-status { background-color: #ffdada; color: #9b2c2c; } /* Annulee */
        .badge-status.gray-status { background-color: #e9ecef; color: #4a5568; } /* Autre */
        
        .chart-container {
            position: relative;
            height: 400px; /* Hauteur fixe pour le graphique */
            width: 100%;
            margin-top: 2rem;
            margin-bottom: 2rem;
        }
    </style>
</head>
<body>
    <jsp:include page="navbar.jsp" />

    <div class="dashboard-container">
        <h1 class="text-3xl font-bold mb-6 text-gray-800">
            <i class="fas fa-tachometer-alt mr-2"></i> Tableau de Bord Gestionnaire
        </h1>

        <div class="grid-3-cols">
            <div class="card">
                <div class="card-header">
                    <i class="fas fa-car mr-2"></i> Total Voitures
                </div>
                <div class="card-body">
                    <p class="card-metric">${nombreTotalVoitures}</p>
                </div>
            </div>
            <div class="card">
                <div class="card-header">
                    <i class="fas fa-check-circle mr-2"></i> Voitures Disponibles
                </div>
                <div class="card-body">
                    <p class="card-metric">${nombreVoituresDisponibles}</p>
                </div>
            </div>
            <div class="card">
                <div class="card-header">
                    <i class="fas fa-handshake mr-2"></i> Voitures Louées
                </div>
                <div class="card-body">
                    <p class="card-metric">${nombreVoituresLouees}</p>
                </div>
            </div>
            <div class="card">
                <div class="card-header">
                    <i class="fas fa-hourglass-half mr-2"></i> Demandes en Attente
                </div>
                <div class="card-body">
                    <p class="card-metric">${pendingRequestsCount}</p>
                </div>
            </div>
        </div>

        <h2 class="section-title"><i class="fas fa-chart-bar mr-2"></i> Statistiques du Parc Automobile</h2>
        <div class="card">
            <div class="chart-container">
                <canvas id="carStatusChart"></canvas>
            </div>
        </div>

        <!-- Tableau des Voitures actuellement louées (si pertinent pour le gestionnaire) -->
        <h2 class="section-title"><i class="fas fa-car-alt mr-2"></i> Voitures Actuellement Louées</h2>
        <div class="card">
            <div class="table-responsive">
                <c:choose>
                    <c:when test="${not empty voituresLoueesAvecInfosLocataires}">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Immatriculation</th>
                                    <th>Marque</th>
                                    <th>Modèle</th>
                                    <th>Client (CIN)</th>
                                    <th>Date Début</th>
                                    <th>Date Retour Prévue</th>
                                    <th>Statut</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="location" items="${voituresLoueesAvecInfosLocataires}">
                                    <tr>
                                        <td>${location.voiture.immatriculation}</td>
                                        <td>${location.voiture.marque}</td>
                                        <td>${location.voiture.modele}</td>
                                        <td>${location.client.prenom} ${location.client.nom} (${location.client.cin})</td>
                                        <td><fmt:formatDate value="${location.legacyDateDebut}" pattern="dd/MM/yyyy" /></td>
                                        <td><fmt:formatDate value="${location.legacyDateRetourPrevue}" pattern="dd/MM/yyyy" /></td>
                                        <td>
                                            <span class="badge-status <c:if test='${location.statut eq "En cours"}'>blue</c:if><c:if test='${location.statut eq "En attente"}'>yellow</c:if><c:if test='${location.statut eq "Terminee"}'>green-status</c:if><c:if test='${location.statut eq "Annulee"}'>red-status</c:if>">
                                                ${location.statut}
                                            </span>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <p class="text-center text-gray-600">Aucune voiture actuellement louée à afficher.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <!-- Section des Demandes en Attente (si les données sont disponibles) -->
        <h2 class="section-title"><i class="fas fa-clock mr-2"></i> Demandes de Location en Attente</h2>
        <div class="card">
            <div class="table-responsive">
                <c:choose>
                    <c:when test="${not empty pendingRequests}">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>ID Location</th>
                                    <th>Client (CIN)</th>
                                    <th>Voiture (Immatriculation)</th>
                                    <th>Date Début</th>
                                    <th>Jours</th>
                                    <th>Montant Estimé</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="req" items="${pendingRequests}">
                                    <tr>
                                        <td>${req.id}</td>
                                        <td>${req.client.prenom} ${req.client.nom} (${req.client.cin})</td>
                                        <td>${req.voiture.marque} ${req.voiture.modele} (${req.voiture.immatriculation})</td>
                                        <td><fmt:formatDate value="${req.legacyDateDebut}" pattern="dd/MM/yyyy" /></td>
                                        <td>${req.nombreJours}</td>
                                        <td><fmt:formatNumber value="${req.montantTotal}" type="currency" currencySymbol="€" maxFractionDigits="2" minFractionDigits="2" /></td>
                                        <td class="action-buttons-group">
                                            <!-- Liens pour Valider / Annuler (à implémenter dans LocationServlet) -->
                                            <a href="${pageContext.request.contextPath}/locations?action=validate&id=${req.id}" class="btn btn-success">Valider</a>
                                            <a href="${pageContext.request.contextPath}/locations?action=cancel&id=${req.id}" class="btn btn-danger">Annuler</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <p class="text-center text-gray-600">Aucune demande de location en attente.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        
        <!-- Section des Voitures Disponibles (si les données sont disponibles) -->
        <h2 class="section-title"><i class="fas fa-car-side mr-2"></i> Voitures Disponibles</h2>
        <div class="card">
            <div class="table-responsive">
                <c:choose>
                    <c:when test="${not empty availableCars}">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Immatriculation</th>
                                    <th>Marque</th>
                                    <th>Modèle</th>
                                    <th>Places</th>
                                    <th>Kilométrage</th>
                                    <th>Prix/Jour (€)</th>
                                    <th>Catégorie</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="car" items="${availableCars}">
                                    <tr>
                                        <td>${car.immatriculation}</td>
                                        <td>${car.marque}</td>
                                        <td>${car.modele}</td>
                                        <td>${car.nbPlaces}</td>
                                        <td><fmt:formatNumber value="${car.kilometrage}" type="number" maxFractionDigits="0" /> km</td>
                                        <td><fmt:formatNumber value="${car.prixLocationJ}" type="currency" currencySymbol="€" maxFractionDigits="0" minFractionDigits="0" /></td>
                                        <td>${car.categorie}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <p class="text-center text-gray-600">Aucune voiture disponible à afficher.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

    </div>

    <script>
        // Script pour le graphique (s'exécute après le chargement du DOM)
        document.addEventListener('DOMContentLoaded', function() {
            // Récupération des données depuis les attributs de requête
            const totalCars = ${nombreTotalVoitures};
            const availableCars = ${nombreVoituresDisponibles};
            const rentedCars = ${nombreVoituresLouees};
            const pendingRequests = ${pendingRequestsCount}; // Le nombre, pas la liste

            const ctx = document.getElementById('carStatusChart').getContext('2d');
            new Chart(ctx, {
                type: 'doughnut', // Ou 'pie' si vous préférez
                data: {
                    labels: ['Voitures Disponibles', 'Voitures Louées', 'Demandes en Attente'],
                    datasets: [{
                        label: 'Statut du Parc Automobile',
                        data: [availableCars, rentedCars, pendingRequests],
                        backgroundColor: [
                            'rgba(75, 192, 192, 0.8)', // Vert-bleu pour disponible
                            'rgba(255, 99, 132, 0.8)', // Rouge pour loué
                            'rgba(255, 206, 86, 0.8)'  // Jaune pour en attente
                        ],
                        borderColor: [
                            'rgba(75, 192, 192, 1)',
                            'rgba(255, 99, 132, 1)',
                            'rgba(255, 206, 86, 1)'
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            position: 'top',
                        },
                        title: {
                            display: true,
                            text: 'Répartition des Statuts des Véhicules',
                            font: {
                                size: 16
                            }
                        }
                    }
                }
            });
        });
    </script>
</body>
</html>
