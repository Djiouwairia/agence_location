<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="com.agence.location.model.Voiture" %>
<%@ page import="com.agence.location.model.Client" %>
<%@ page import="com.agence.location.dto.RentalFormData" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Détails de la Voiture et Demande de Location</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        .card-container {
            max-width: 900px;
            margin: 2rem auto;
            padding: 2rem;
            background-color: #ffffff;
            border-radius: 8px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            display: flex;
            flex-wrap: wrap;
            gap: 2rem;
        }
        .car-details {
            flex: 1;
            min-width: 300px;
        }
        .rental-form {
            flex: 1;
            min-width: 300px;
        }
        .car-details h2, .rental-form h2 {
            font-size: 1.8rem;
            color: #2d3748;
            margin-bottom: 1.5rem;
            border-bottom: 2px solid #edf2f7;
            padding-bottom: 0.5rem;
        }
        .detail-item {
            margin-bottom: 0.75rem;
        }
        .detail-item strong {
            color: #4a5568;
            margin-right: 0.5rem;
        }
        .form-group {
            margin-bottom: 1rem;
        }
        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: bold;
            color: #2d3748;
        }
        .form-group input[type="date"],
        .form-group input[type="number"],
        .form-group input[type="text"] {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #cbd5e0;
            border-radius: 6px;
            box-sizing: border-box;
            font-size: 1rem;
        }
        .form-group input[type="date"]:focus,
        .form-group input[type="number"]:focus,
        .form-group input[type="text"]:focus {
            outline: none;
            border-color: #3182ce;
            box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.5);
        }
        .btn-submit {
            background-color: #007bff;
            color: white;
            padding: 0.75rem 1.5rem;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 1rem;
            font-weight: bold;
            transition: background-color 0.2s ease-in-out;
            width: 100%;
        }
        .btn-submit:hover {
            background-color: #0056b3;
        }
        .message-box {
            background-color: #d4edda;
            color: #155724;
            padding: 1rem;
            border-radius: 5px;
            margin-bottom: 1.5rem;
            border: 1px solid #c3e6cb;
        }
        .error-message {
            background-color: #f8d7da;
            color: #721c24;
            padding: 1rem;
            border-radius: 5px;
            margin-bottom: 1.5rem;
            border: 1px solid #f5c6cb;
        }
    </style>
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="card-container">
        <c:if test="${not empty requestScope.message}">
            <p class="message-box">${requestScope.message}</p>
        </c:if>
        <c:if test="${not empty requestScope.error}">
            <p class="error-message">${requestScope.error}</p>
        </c:if>

        <c:choose>
            <c:when test="${not empty requestScope.voiture}">
                <div class="car-details">
                    <h2>Détails de la Voiture</h2>
                    <div class="detail-item"><strong>Marque:</strong> <c:out value="${requestScope.voiture.marque}"/></div>
                    <div class="detail-item"><strong>Modèle:</strong> <c:out value="${requestScope.voiture.modele}"/></div>
                    <div class="detail-item"><strong>Immatriculation:</strong> <c:out value="${requestScope.voiture.immatriculation}"/></div>
                    <div class="detail-item"><strong>Nombre de Places:</strong> <c:out value="${requestScope.voiture.nbPlaces}"/></div>
                    <div class="detail-item"><strong>Année de Circulation:</strong> <c:out value="${requestScope.voiture.dateMiseCirculation}"/></div>
                    <div class="detail-item"><strong>Kilométrage:</strong> <c:out value="${requestScope.voiture.kilometrage}"/> km</div>
                    <div class="detail-item"><strong>Type de Carburant:</strong> <c:out value="${requestScope.voiture.typeCarburant}"/></div>
                    <div class="detail-item"><strong>Catégorie:</strong> <c:out value="${requestScope.voiture.categorie}"/></div>
                    <div class="detail-item"><strong>Prix par Jour:</strong> <c:out value="${requestScope.voiture.prixLocationJ}"/> €</div>
                    <div class="detail-item"><strong>Statut:</strong> <c:out value="${requestScope.voiture.statut}"/></div>
                </div>

                <div class="rental-form">
                    <h2>Demande de Location</h2>
                    <c:set var="today" value="<%= LocalDate.now() %>"/>
                    <form action="clientRental" method="post">
                        <input type="hidden" name="action" value="request"/>
                        <input type="hidden" name="immatriculationVoiture" value="${requestScope.voiture.immatriculation}"/>

                        <div class="form-group">
                            <label for="dateDebut">Date de Début :</label>
                            <input type="date" id="dateDebut" name="dateDebut" required
                                   min="<c:out value="${today}"/>"
                                   value="<c:out value="${requestScope.formData.dateDebut != null ? requestScope.formData.dateDebut : today}"/>">
                        </div>

                        <div class="form-group">
                            <label for="nombreJours">Nombre de Jours :</label>
                            <input type="number" id="nombreJours" name="nombreJours" min="1" required
                                   value="<c:out value="${requestScope.formData.nombreJours != null ? requestScope.formData.nombreJours : '1'}"/>">
                        </div>

                        <div class="form-group">
                            <label for="montantTotalEstime">Montant Total Estimé (€) :</label>
                            <%-- CHAMP POUR L'AFFICHAGE UNIQUEMENT - NE PAS LUI DONNER UN ATTRIBUT 'NAME' --%>
                            <input type="text" id="montantTotalEstimeAffichage" readonly
                                   value="<fmt:formatNumber value="${requestScope.formData.montantTotalEstime != null ? requestScope.formData.montantTotalEstime : 0}" pattern="#,##0.00"/> €">
                            
                            <%-- CHAMP CACHÉ POUR ENVOYER LA VALEUR NUMÉRIQUE PROPRE À LA SERVLET --%>
                            <input type="hidden" id="montantTotalEstime" name="montantTotalEstime"
                                   value="<c:out value="${requestScope.formData.montantTotalEstime != null ? requestScope.formData.montantTotalEstime : 0}"/>"/>
                            
                            <input type="hidden" id="prixLocationJ" value="${requestScope.voiture.prixLocationJ}"/>
                        </div>

                        <button type="submit" class="btn-submit"><i class="fas fa-paper-plane mr-2"></i> Soumettre la Demande</button>
                    </form>
                </div>
            </c:when>
            <c:otherwise>
                <p class="error-message">Désolé, aucune voiture trouvée pour les détails demandés.</p>
            </c:otherwise>
        </c:choose>
    </div>

    <script src="js/main.js"></script>
    <script>
        // Fonction pour calculer le montant total estimé
        function calculateEstimatedTotal() {
            const prixJour = parseFloat(document.getElementById('prixLocationJ').value);
            const nombreJours = parseInt(document.getElementById('nombreJours').value);
            const montantTotalEstimeAffichageInput = document.getElementById('montantTotalEstimeAffichage'); // Pour l'affichage
            const montantTotalEstimeHiddenInput = document.getElementById('montantTotalEstime'); // Pour la soumission

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
            
            // Calcul initial au chargement de la page
            calculateEstimatedTotal();

            // Écouteurs d'événements pour recalculer quand les valeurs changent
            if (nombreJoursInput) {
                nombreJoursInput.addEventListener('input', calculateEstimatedTotal);
            }
        });
    </script>
</body>
</html>
