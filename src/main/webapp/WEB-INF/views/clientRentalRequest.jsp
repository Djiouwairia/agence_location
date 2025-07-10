<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="java.time.LocalDate" %>
<%-- Importe RentalFormData si tu l'utilises pour repopuler le formulaire --%>
<%@ page import="com.agence.location.dto.RentalFormData" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Soumettre une Demande de Location</title>
    <link rel="stylesheet" href="css/style.css"> 
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
<jsp:include page="navbar.jsp"/>

    <div class="content-area flex items-center justify-center min-h-screen-minus-navbar">
        <main class="main-content-card w-full max-w-2xl">
            <h2 class="dashboard-heading text-3xl font-bold text-gray-800 mb-6 text-center">Soumettre une Demande de Location</h2>

            <%-- Message de succès ou d'erreur --%>
            <c:if test="${not empty requestScope.message}">
                <p class="success-message">${requestScope.message}</p>
            </c:if>
            <c:if test="${not empty requestScope.error}">
                <p class="error-message">${requestScope.error}</p>
            </c:if>

            <c:if test="${not empty requestScope.selectedVoiture}">
                <div class="dashboard-section card mb-8 p-6 text-center">
                    <h3 class="card-title text-2xl font-semibold mb-4">Voiture sélectionnée</h3>
                    <%-- Image de la voiture --%>
                    <img src="https://placehold.co/400x250/3b82f6/ffffff?text=<c:out value="${requestScope.selectedVoiture.marque}"/>+<c:out value="${requestScope.selectedVoiture.modele}"/>"
                         alt="Image de <c:out value="${requestScope.selectedVoiture.marque}"/> <c:out value="${requestScope.selectedVoiture.modele}"/>"
                         class="w-full h-48 object-cover rounded-lg mb-4 mx-auto"
                         onerror="this.onerror=null;this.src='https://placehold.co/400x250/cccccc/333333?text=Voiture';">
                    
                    <p class="text-lg mb-2"><strong>Marque:</strong> <c:out value="${requestScope.selectedVoiture.marque}"/></p>
                    <p class="text-lg mb-2"><strong>Modèle:</strong> <c:out value="${requestScope.selectedVoiture.modele}"/></p>
                    <p class="text-lg mb-2"><strong>Immatriculation:</strong> <c:out value="${requestScope.selectedVoiture.immatriculation}"/></p>
                    <p class="text-lg mb-2"><strong>Catégorie:</strong> <c:out value="${requestScope.selectedVoiture.categorie}"/></p>
                    <p class="text-2xl font-bold text-green-600 mt-4">Prix par jour: <fmt:formatNumber value="${requestScope.selectedVoiture.prixLocationJ}" pattern="#,##0.00" /> €</p>
                    <p class="text-sm text-gray-600 mt-2">Le kilométrage actuel (<fmt:formatNumber value="${requestScope.selectedVoiture.kilometrage}" pattern="#,##0" /> km) sera enregistré au moment de la validation par le gestionnaire.</p>
                </div>

                <form action="clientRental" method="post" class="space-y-6">
                    <input type="hidden" name="action" value="request">
                    <input type="hidden" name="immatriculationVoiture" value="<c:out value="${requestScope.selectedVoiture.immatriculation}"/>">
                    <%-- Le CIN du client est pris de la session (sessionScope.client.cin), donc pas besoin d'un champ caché --%>

                    <div class="form-group">
                        <label for="dateDebut" class="form-label">Date de début de location :</label>
                        <input type="date" id="dateDebut" name="dateDebut" required
                               min="<%= LocalDate.now().toString() %>" <%-- Date minimale aujourd'hui --%>
                               value="<c:out value="${requestScope.formData.dateDebut != null ? requestScope.formData.dateDebut : ''}"/>"
                               class="form-input">
                        <p class="text-xs text-gray-500 mt-1">La date de début ne peut pas être antérieure à aujourd'hui.</p>
                    </div>
                    <div class="form-group">
                        <label for="nombreJours" class="form-label">Nombre de jours de location :</label>
                        <input type="number" id="nombreJours" name="nombreJours" required min="1"
                               placeholder="Entrez le nombre de jours"
                               value="<c:out value="${requestScope.formData.nombreJours != null ? requestScope.formData.nombreJours : '1'}"/>"
                               class="form-input">
                    </div>
                    
                    <div class="form-group">
                        <label for="montantTotalEstimeAffichage" class="form-label">Montant Total Estimé (€) :</label>
                        <%-- CHAMP POUR L'AFFICHAGE UNIQUEMENT - NE PAS LUI DONNER UN ATTRIBUT 'NAME' --%>
                        <input type="text" id="montantTotalEstimeAffichage" readonly
                               value="<fmt:formatNumber value="${requestScope.formData.montantTotalEstime != null ? requestScope.formData.montantTotalEstime : 0}" pattern="#,##0.00"/> €"
                               class="form-input">
                        
                        <%-- CHAMP CACHÉ POUR ENVOYER LA VALEUR NUMÉRIQUE PROPRE À LA SERVLET --%>
                        <input type="hidden" id="montantTotalEstime" name="montantTotalEstime"
                               value="<c:out value="${requestScope.formData.montantTotalEstime != null ? requestScope.formData.montantTotalEstime : 0}"/>"/>
                        
                        <input type="hidden" id="prixLocationJ" value="<c:out value="${requestScope.selectedVoiture.prixLocationJ}"/>"/>
                    </div>

                    <button type="submit" class="btn-primary w-full">
                        <i class="fas fa-paper-plane mr-2"></i> Soumettre la Demande
                    </button>
                    <a href="clientDashboard?tab=cars" class="block text-center mt-4 text-gray-600 hover:underline">Annuler et Retourner aux Voitures</a>
                </form>
            </c:if>
            <c:if test="${empty requestScope.selectedVoiture}">
                <p class="error-message text-center">Aucune voiture sélectionnée pour la demande de location.</p>
                <a href="clientDashboard?tab=cars" class="block text-center mt-4 text-blue-600 hover:underline">Retour aux Voitures Disponibles</a>
            </c:if>
        </main>
    </div>
<%-- <script src="js/client-dashboard.js"></script> --%>
    <script>
        // Fonction pour calculer le montant total estimé
        function calculateEstimatedTotal() {
            const prixJourInput = document.getElementById('prixLocationJ');
            const nombreJoursInput = document.getElementById('nombreJours');
            const montantTotalEstimeAffichageInput = document.getElementById('montantTotalEstimeAffichage'); // Pour l'affichage
            const montantTotalEstimeHiddenInput = document.getElementById('montantTotalEstime'); // Pour la soumission

            const prixJour = parseFloat(prixJourInput ? prixJourInput.value : '0');
            const nombreJours = parseInt(nombreJoursInput ? nombreJoursInput.value : '0');

            if (!isNaN(prixJour) && !isNaN(nombreJours) && nombreJours > 0) {
                const total = prixJour * nombreJours;
                montantTotalEstimeAffichageInput.value = total.toFixed(2) + ' €'; // Affichage avec le symbole
                montantTotalEstimeHiddenInput.value = total.toFixed(2); // Valeur numérique propre pour l'envoi
            } else {
                montantTotalEstimeAffichageInput.value = '0.00 €';
                montantTotalEstimeHiddenInput.value = '0.00';
            }
        }

        document.addEventListener('DOMContentLoaded', () => {
            const nombreJoursInput = document.getElementById('nombreJours');
            const dateDebutInput = document.getElementById('dateDebut');

            // Calcul initial au chargement de la page
            calculateEstimatedTotal();

            // Écouteurs d'événements pour recalculer quand les valeurs changent
            if (nombreJoursInput) {
                nombreJoursInput.addEventListener('input', calculateEstimatedTotal);
            }
            // Tu peux ajouter un écouteur sur la date de début si tu veux un calcul dynamique basé sur la date de fin aussi
            // mais pour l'instant, le nombre de jours est le facteur principal.
        });
    </script>
</body>
</html>
