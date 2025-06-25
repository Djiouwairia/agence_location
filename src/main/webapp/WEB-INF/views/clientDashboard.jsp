<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.agence.location.model.Client" %>
<%@ page import="com.agence.location.model.Voiture" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tableau de Bord Client - Agence de Location</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        /* Styles spécifiques pour le tableau de bord client */
        .dashboard-container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 2rem;
            background-color: #ffffff;
            border-radius: 8px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
        }
        .dashboard-section {
            margin-bottom: 2rem;
            padding: 1.5rem;
            border: 1px solid #e2e8f0;
            border-radius: 6px;
            background-color: #f7fafc;
        }
        .dashboard-section h3 {
            font-size: 1.5rem;
            color: #2d3748;
            margin-bottom: 1rem;
            border-bottom: 2px solid #edf2f7;
            padding-bottom: 0.5rem;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
        }
        th, td {
            padding: 0.75rem;
            text-align: left;
            border-bottom: 1px solid #e2e8f0;
        }
        th {
            background-color: #edf2f7;
            font-weight: 600;
            color: #4a5568;
            font-size: 0.875rem;
            text-transform: uppercase;
        }
        tr:hover {
            background-color: #f0f4f8;
        }
        .action-buttons {
            display: flex;
            gap: 0.5rem;
        }
        .action-buttons a {
            padding: 0.5rem 1rem;
            border-radius: 4px;
            text-decoration: none;
            color: white;
            font-size: 0.875rem;
            display: inline-flex;
            align-items: center;
        }
        .action-buttons a.btn-primary {
            background-color: #007bff;
        }
        .action-buttons a.btn-primary:hover {
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
    <jsp:include page="navbar.jsp"/> <%-- Utilise la même barre de navigation, potentiellement à adapter --%>

    <div class="dashboard-container">
        <h2 class="text-2xl font-bold mb-6">Bienvenue, <c:out value="${sessionScope.client.prenom}"/> !</h2>

        <%-- Message de succès ou d'erreur --%>
        <c:if test="${not empty requestScope.message}">
            <p class="message-box">${requestScope.message}</p>
        </c:if>
        <c:if test="${not empty requestScope.error}">
            <p class="error-message">${requestScope.error}</p>
        </c:if>

        <div class="dashboard-section">
            <h3>Voitures Disponibles</h3>
            <p>Voici la liste des voitures actuellement disponibles pour la location.</p>

            <c:choose>
                <c:when test="${not empty requestScope.voituresDisponibles}">
                    <table>
                        <thead>
                            <tr>
                                <th>Marque</th>
                                <th>Modèle</th>
                                <th>Nb. Places</th>
                                <th>Année Circ.</th>
                                <th>Kilométrage</th>
                                <th>Carburant</th>
                                <th>Catégorie</th>
                                <th>Prix/Jour</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="voiture" items="${requestScope.voituresDisponibles}">
                                <tr>
                                    <td><c:out value="${voiture.marque}"/></td>
                                    <td><c:out value="${voiture.modele}"/></td>
                                    <td><c:out value="${voiture.nbPlaces}"/></td>
                                    <td><c:out value="${voiture.dateMiseCirculation}"/></td>
                                    <td><c:out value="${voiture.kilometrage}"/> km</td>
                                    <td><c:out value="${voiture.typeCarburant}"/></td>
                                    <td><c:out value="${voiture.categorie}"/></td>
                                    <td><c:out value="${voiture.prixLocationJ}"/> €</td>
                                    <td class="action-buttons">
                                        <%-- MODIFICATION IMPORTANTE ICI: Le lien pointe maintenant vers ClientVoitureServlet --%>
                                        <a href="clientVoitures?action=viewDetails&immatriculation=${voiture.immatriculation}" class="btn-primary">
                                            <i class="fas fa-handshake mr-1"></i> Demander
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <p>Aucune voiture n'est disponible pour le moment. Veuillez vérifier plus tard.</p>
                </c:otherwise>
            </c:choose>
        </div>

        <%-- Vous pouvez ajouter d'autres sections ici, par exemple, "Mes locations en cours" --%>
        <div class="dashboard-section">
            <h3>Mes Demandes de Location</h3>
            <p>Ici, vous pourrez voir le statut de vos demandes de location.</p>
            <%-- Les demandes de location du client seront affichées ici, après implémentation --%>
            <c:choose>
                <c:when test="${not empty requestScope.clientLocations}">
                    <table>
                        <thead>
                        <tr>
                            <th>Voiture</th>
                            <th>Date Début</th>
                            <th>Jours Prévus</th>
                            <th>Montant Est.</th>
                            <th>Statut</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="location" items="${requestScope.clientLocations}">
                            <tr>
                                <td><c:out value="${location.voiture.marque}"/> <c:out value="${location.voiture.modele}"/> (<c:out value="${location.voiture.immatriculation}"/>)</td>
                                <td><c:out value="${location.dateDebut}"/></td>
                                <td><c:out value="${location.nombreJours}"/></td>
                                <td><c:out value="${location.montantTotal}"/> €</td>
                                <td>
                                    <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full
                                        <c:choose>
                                            <c:when test="${location.statut eq 'En attente'}">bg-yellow-100 text-yellow-800</c:when>
                                            <c:when test="${location.statut eq 'En cours'}">bg-blue-100 text-blue-800</c:when>
                                            <c:when test="${location.statut eq 'Terminee'}">bg-green-100 text-green-800</c:when>
                                            <c:when test="${location.statut eq 'Annulee'}">bg-red-100 text-red-800</c:when>
                                            <c:otherwise>bg-gray-100 text-gray-800</c:otherwise>
                                        </c:choose>
                                    ">
                                        <c:out value="${location.statut}"/>
                                    </span>
                                </td>
                                <td>
                                    <c:if test="${location.statut eq 'En attente'}">
                                        <a href="clientRental?action=cancelRequest&id=${location.id}" class="text-red-600 hover:text-red-900">Annuler</a>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <p>Vous n'avez pas encore de demandes de location ou de locations en cours.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>
