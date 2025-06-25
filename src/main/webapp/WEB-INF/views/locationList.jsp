<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%-- Aucune importation Java explicite n'est nécessaire ici pour les modèles si JSTL est bien configuré --%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Liste des Locations</title>
    <link rel="stylesheet" href="css/style.css"> <%-- Votre fichier CSS externe --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="dashboard-container">
        <h2 class="text-2xl font-bold mb-6 text-center">Gestion des Locations</h2>

        <%-- Message de succès depuis la session --%>
        <c:if test="${not empty sessionScope.message}">
            <p class="success-message">${sessionScope.message}</p>
            <c:remove var="message" scope="session"/> <%-- Supprime le message après affichage --%>
        </c:if>
        <%-- Message d'erreur depuis la session --%>
        <c:if test="${not empty sessionScope.error}">
            <p class="error-message">${sessionScope.error}</p>
            <c:remove var="error" scope="session"/> <%-- Supprime l'erreur après affichage --%>
        </c:if>

        <div class="mb-4 flex space-x-2 justify-between items-center"> <%-- Ajout de justify-between et items-center pour espacer les boutons --%>
            <a href="locations?action=new" class="inline-block bg-green-600 text-white py-2 px-4 rounded-md hover:bg-green-700">
                <i class="fas fa-plus-circle mr-2"></i> Enregistrer une nouvelle location
            </a>
            
            <%-- BOUTON POUR TÉLÉCHARGER LA LISTE DES LOCATIONS --%>
            <a href="locations?action=exportList" class="inline-block bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700">
                <i class="fas fa-file-pdf mr-2"></i> Télécharger la liste PDF
            </a>
        </div>

        <c:choose>
            <c:when test="${not empty requestScope.locations}">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Client (CIN)</th>
                            <th>Voiture (Immat.)</th>
                            <th>Gestionnaire</th>
                            <th>Date Début</th>
                            <th>Jours</th>
                            <th>Date Retour Prévue</th>
                            <th>Date Retour Réelle</th>
                            <th>Montant Total</th>
                            <th>Statut</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="location" items="${requestScope.locations}">
                            <tr>
                                <td><c:out value="${location.id}"/></td>
                                <td><c:out value="${location.client.cin}"/> - <c:out value="${location.client.nom}"/></td>
                                <td><c:out value="${location.voiture.immatriculation}"/> - <c:out value="${location.voiture.marque}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty location.utilisateur}">
                                            <c:out value="${location.utilisateur.prenom}"/> <c:out value="${location.utilisateur.nom}"/>
                                        </c:when>
                                        <c:otherwise>N/A</c:otherwise>
                                    </c:choose>
                                </td>
                                <td><fmt:formatDate value="${location.legacyDateDebut}" pattern="dd/MM/yyyy" /></td>
                                <td><c:out value="${location.nombreJours}"/></td>
                                <td><fmt:formatDate value="${location.legacyDateRetourPrevue}" pattern="dd/MM/yyyy" /></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty location.legacyDateRetourReelle}">
                                            <fmt:formatDate value="${location.legacyDateRetourReelle}" pattern="dd/MM/yyyy" />
                                        </c:when>
                                        <c:otherwise>N/A</c:otherwise>
                                    </c:choose>
                                </td>
                                <td><fmt:formatNumber value="${location.montantTotal}" pattern="#,##0.00" /> €</td>
                                <td>
                                    <span class="badge-status
                                        <c:choose>
                                            <c:when test="${location.statut eq 'En attente'}">yellow</c:when>
                                            <c:when test="${location.statut eq 'En cours'}">blue</c:when>
                                            <c:when test="${location.statut eq 'Terminee'}">green-status</c:when>
                                            <c:when test="${location.statut eq 'Annulee'}">red-status</c:when>
                                            <c:otherwise>gray-status</c:otherwise>
                                        </c:choose>
                                    ">
                                        <c:out value="${location.statut}"/>
                                    </span>
                                </td>
                                <td class="action-buttons-group">
                                    <c:choose>
                                        <c:when test="${location.statut eq 'En attente'}">
                                            <a href="locations?action=acceptRequest&id=${location.id}" class="bg-green-500 text-white hover:bg-green-600">
                                                <i class="fas fa-check"></i> Accepter
                                            </a>
                                            <a href="locations?action=declineRequest&id=${location.id}" class="bg-red-500 text-white hover:bg-red-600">
                                                <i class="fas fa-times"></i> Décliner
                                            </a>
                                        </c:when>
                                        <c:when test="${location.statut eq 'En cours'}">
                                            <a href="locations?action=return&id=${location.id}" class="bg-yellow-500 text-white hover:bg-yellow-600">
                                                <i class="fas fa-undo"></i> Retourner
                                            </a>
                                            <a href="locations?action=generateInvoice&locationId=${location.id}" class="bg-gray-700 text-white hover:bg-gray-800">
                                                <i class="fas fa-file-pdf"></i> Facture PDF
                                            </a>
                                        </c:when>
                                        <c:when test="${location.statut eq 'Terminee'}">
                                            <a href="locations?action=generateInvoice&locationId=${location.id}" class="bg-gray-700 text-white hover:bg-gray-800">
                                                <i class="fas fa-file-pdf"></i> Facture PDF
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-gray-500">N/A</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p class="text-gray-600 text-center py-4">Aucune location enregistrée.</p>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
 	