<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Enregistrer une Nouvelle Location</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        .search-results {
            max-height: 200px;
            overflow-y: auto;
            border: 1px solid #e2e8f0;
            border-radius: 0.375rem;
            margin-top: 0.5rem;
        }
        .search-results div {
            padding: 0.5rem 0.75rem;
            cursor: pointer;
            border-bottom: 1px solid #edf2f7;
        }
        .search-results div:last-child {
            border-bottom: none;
        }
        .search-results div:hover {
            background-color: #f7fafc;
        }
    </style>
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="dashboard-container">
        <h2 class="text-2xl font-bold mb-6">Enregistrer une Nouvelle Location</h2>

        <%-- Message de succès ou d'erreur --%>
        <c:if test="${not empty requestScope.message}">
            <p class="success-message">${requestScope.message}</p>
        </c:if>
        <c:if test="${not empty requestScope.error}">
            <p class="error-message">${requestScope.error}</p>
        </c:if>

        <form action="locations" method="post" class="space-y-6">
            <input type="hidden" name="action" value="add">

            <%-- Section Sélection du Client --%>
            <div class="dashboard-section">
                <h3>1. Sélectionner le Client</h3>
                <div class="space-y-4">
                    <div>
                        <label for="clientCin" class="block text-sm font-medium text-gray-700">CIN du Client :</label>
                        <input type="text" id="clientCin" name="clientCin" value="${selectedClient.cin}" required
                               class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm"
                               onchange="fetchClientDetails(this.value)">
                    </div>
                    <div id="clientDetails">
                        <c:if test="${selectedClient != null}">
                            <p><strong>Nom :</strong> ${selectedClient.prenom} ${selectedClient.nom}</p>
                            <p><strong>Téléphone :</strong> ${selectedClient.telephone}</p>
                            <p><strong>Email :</strong> ${selectedClient.email}</p>
                        </c:if>
                        <c:if test="${selectedClient == null && not empty param.clientCin}">
                            <p class="error-message">Client avec CIN "${param.clientCin}" non trouvé.</p>
                        </c:if>
                    </div>
                </div>
            </div>

            <%-- Section Recherche et Sélection de la Voiture --%>
            <div class="dashboard-section">
                <h3>2. Rechercher et Sélectionner la Voiture</h3>
                <div class="space-y-4">
                    <div class="flex space-x-2">
                        <div>
                            <label for="marque" class="block text-sm font-medium text-gray-700">Marque :</label>
                            <input type="text" id="marque" name="marque" class="mt-1 px-3 py-2 border rounded-md">
                        </div>
                        <div>
                            <label for="categorie" class="block text-sm font-medium text-gray-700">Catégorie :</label>
                            <input type="text" id="categorie" name="categorie" class="mt-1 px-3 py-2 border rounded-md">
                        </div>
                        <button type="button" onclick="searchCars()" class="bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 self-end">Rechercher</button>
                    </div>

                    <div id="voitureSearchResults" class="search-results">
                        <%-- Les résultats de recherche de voitures seront affichés ici --%>
                        <c:if test="${not empty requestScope.availableCars}">
                            <p>Voitures disponibles :</p>
                            <c:forEach var="car" items="${requestScope.availableCars}">
                                <div onclick="selectVoiture('${car.immatriculation}', '${car.marque} ${car.modele}', ${car.prixLocationJ})">
                                    ${car.immatriculation} - ${car.marque} ${car.modele} (${car.prixLocationJ} €/jour) - Places: ${car.nbPlaces} - Km: ${car.kilometrage}
                                </div>
                            </c:forEach>
                        </c:if>
                        <c:if test="${empty requestScope.availableCars && requestScope.availableCars != null}">
                             <p>Aucune voiture disponible ne correspond à ces critères.</p>
                        </c:if>
                    </div>

                    <div>
                        <label for="voitureImmat" class="block text-sm font-medium text-gray-700">Immatriculation Voiture Sélectionnée :</label>
                        <input type="text" id="voitureImmat" name="voitureImmat" value="${selectedVoiture.immatriculation}" readonly required
                               class="mt-1 block w-full px-3 py-2 border rounded-md bg-gray-100">
                    </div>
                    <div>
                        <label for="voitureDetails" class="block text-sm font-medium text-gray-700">Détails Voiture :</label>
                        <input type="text" id="voitureDetails" value="<c:if test="${selectedVoiture != null}">${selectedVoiture.marque} ${selectedVoiture.modele} (${selectedVoiture.prixLocationJ} €/jour)</c:if>" readonly
                               class="mt-1 block w-full px-3 py-2 border rounded-md bg-gray-100">
                        <input type="hidden" id="prixLocationJ" value="<c:if test="${selectedVoiture != null}">${selectedVoiture.prixLocationJ}</c:if>">
                    </div>
                </div>
            </div>

            <%-- Section Détails de la Location --%>
            <div class="dashboard-section">
                <h3>3. Détails de la Location</h3>
                <div class="space-y-4">
                    <div>
                        <label for="dateDebut" class="block text-sm font-medium text-gray-700">Date de Début :</label>
                        <input type="date" id="dateDebut" name="dateDebut" value="<%= java.time.LocalDate.now() %>" required
                               class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm">
                    </div>
                    <div>
                        <label for="nombreJours" class="block text-sm font-medium text-gray-700">Nombre de jours :</label>
                        <input type="number" id="nombreJours" name="nombreJours" min="1" value="1" required
                               class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm"
                               oninput="calculateTotal()">
                    </div>
                    <div>
                        <label for="montantTotal" class="block text-sm font-medium text-gray-700">Montant Total :</label>
                        <input type="text" id="montantTotal" name="montantTotal" value="0.00 €" readonly
                               class="mt-1 block w-full px-3 py-2 border rounded-md bg-gray-100">
                    </div>
                </div>
            </div>

            <button type="submit" class="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700">
                Enregistrer la Location
            </button>
            <a href="locations" class="block text-center mt-2 text-gray-600 hover:underline">Annuler et Retour à la liste</a>
        </form>
    </div>

    <script>
        // Fonction pour calculer le montant total en temps réel
        function calculateTotal() {
            const prixJour = parseFloat(document.getElementById('prixLocationJ').value || '0');
            const nombreJours = parseInt(document.getElementById('nombreJours').value || '0');
            const montantTotal = prixJour * nombreJours;
            document.getElementById('montantTotal').value = montantTotal.toFixed(2) + ' €';
        }

        // Fonction pour sélectionner une voiture des résultats de recherche
        function selectVoiture(immatriculation, details, prix) {
            document.getElementById('voitureImmat').value = immatriculation;
            document.getElementById('voitureDetails').value = details;
            document.getElementById('prixLocationJ').value = prix;
            calculateTotal(); // Recalculer le total après sélection de voiture
        }

        // Fonction pour rechercher les voitures disponibles via AJAX (ou simple redirection pour cet exemple)
        function searchCars() {
            const marque = document.getElementById('marque').value;
            const categorie = document.getElementById('categorie').value;
            const clientCin = document.getElementById('clientCin').value; // Conserver le CIN du client
            window.location.href = `locations?action=searchAvailableCars&marque=${marque}&categorie=${categorie}&clientCin=${clientCin}`;
        }

        // Fonction pour récupérer les détails du client via AJAX (simple pour cet exemple)
        // Dans une application réelle, vous feriez un appel AJAX à une servlet dédiée pour cela
        function fetchClientDetails(cin) {
            if (cin) {
                // Pour cet exemple simple, nous allons juste afficher un message.
                // Dans une vraie application, vous feriez un fetch() ou XMLHttpRequest ici.
                // Exemple (conceptuel, non fonctionnel sans backend AJAX dédié pour les clients) :
                /*
                fetch(`clients?action=getDetails&cin=${cin}`)
                    .then(response => response.json())
                    .then(data => {
                        if (data) {
                            document.getElementById('clientDetails').innerHTML = `
                                <p><strong>Nom :</strong> ${data.prenom} ${data.nom}</p>
                                <p><strong>Téléphone :</strong> ${data.telephone}</p>
                                <p><strong>Email :</strong> ${data.email}</p>
                            `;
                        } else {
                            document.getElementById('clientDetails').innerHTML = '<p class="error-message">Client non trouvé.</p>';
                        }
                    })
                    .catch(error => console.error('Erreur:', error));
                */
                // Pour l'instant, on va juste s'assurer que la page est rechargée avec le client
                // si le CIN est entré, pour que le servlet puisse le pré-remplir.
                // Cela est déjà géré par la logique du `doGet` pour le param `clientCin`.
            }
        }

        // Appeler calculateTotal() au chargement de la page si des valeurs sont déjà présentes
        document.addEventListener('DOMContentLoaded', calculateTotal);
    </script>
</body>
</html>
    