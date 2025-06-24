<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <%-- Correction JSTL ici --%>
        <c:choose>
            <c:when test="${voiture != null}">Modifier</c:when>
            <c:otherwise>Ajouter</c:otherwise>
        </c:choose>
        Voiture
    </title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="dashboard-container">
        <h2 class="text-2xl font-bold mb-6">
            <%-- Correction JSTL ici aussi --%>
            <c:choose>
                <c:when test="${voiture != null}">Modifier</c:when>
                <c:otherwise>Ajouter</c:otherwise>
            </c:choose>
            une Voiture
        </h2>

        <%-- Message de succès ou d'erreur --%>
        <c:if test="${not empty requestScope.message}">
            <p class="success-message">${requestScope.message}</p>
        </c:if>
        <c:if test="${not empty requestScope.error}">
            <p class="error-message">${requestScope.error}</p>
        </c:if>

        <form action="voitures" method="post" class="space-y-4">
            <c:if test="${voiture != null}">
                <input type="hidden" name="action" value="update">
            </c:if>
            <c:if test="${voiture == null}">
                <input type="hidden" name="action" value="add">
            </c:if>

            <div>
                <label for="immatriculation" class="block text-sm font-medium text-gray-700">Numéro d'immatriculation :</label>
                <input type="text" id="immatriculation" name="immatriculation" value="${voiture.immatriculation}" required
                       <c:if test="${voiture != null}">readonly</c:if>
                       class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm">
                <c:if test="${voiture != null}"><p class="text-xs text-gray-500 mt-1">L'immatriculation ne peut pas être modifiée.</p></c:if>
            </div>
            <div>
                <label for="nbPlaces" class="block text-sm font-medium text-gray-700">Nombre de places :</label>
                <input type="number" id="nbPlaces" name="nbPlaces" value="${voiture.nbPlaces}" required min="1"
                       class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm">
            </div>
            <div>
                <label for="marque" class="block text-sm font-medium text-gray-700">Marque :</label>
                <input type="text" id="marque" name="marque" value="${voiture.marque}" required
                       class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm">
            </div>
            <div>
                <label for="modele" class="block text-sm font-medium text-gray-700">Modèle :</label>
                <input type="text" id="modele" name="modele" value="${voiture.modele}" required
                       class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm">
            </div>
            <div>
                <label for="dateMiseCirculation" class="block text-sm font-medium text-gray-700">Date de mise en circulation :</label>
                <input type="date" id="dateMiseCirculation" name="dateMiseCirculation"
                       value="${voiture.dateMiseCirculation != null ? voiture.dateMiseCirculation : ''}" required
                       class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm">
            </div>
            <div>
                <label for="kilometrage" class="block text-sm font-medium text-gray-700">Kilométrage :</label>
                <input type="number" id="kilometrage" name="kilometrage" value="${voiture.kilometrage}" required step="0.1" min="0"
                       class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm">
            </div>
            <div>
                <label for="typeCarburant" class="block text-sm font-medium text-gray-700">Type de carburant :</label>
                <select id="typeCarburant" name="typeCarburant" class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm" required>
                    <option value="">Sélectionner</option> <%-- Option vide pour forcer la sélection --%>
                    <option value="Essence" <c:if test="${voiture.typeCarburant eq 'Essence'}">selected</c:if>>Essence</option>
                    <option value="Diesel" <c:if test="${voiture.typeCarburant eq 'Diesel'}">selected</c:if>>Diesel</option>
                    <option value="Électrique" <c:if test="${voiture.typeCarburant eq 'Électrique'}">selected</c:if>>Électrique</option>
                    <option value="Hybride" <c:if test="${voiture.typeCarburant eq 'Hybride'}">selected</c:if>>Hybride</option>
                </select>
            </div>
            <div>
                <label for="categorie" class="block text-sm font-medium text-gray-700">Catégorie :</label>
                <select id="categorie" name="categorie" class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm" required>
                    <option value="">Sélectionner</option> <%-- Option vide pour forcer la sélection --%>
                    <option value="Compacte" <c:if test="${voiture.categorie eq 'Compacte'}">selected</c:if>>Compacte</option>
                    <option value="SUV" <c:if test="${voiture.categorie eq 'SUV'}">selected</c:if>>SUV</option>
                    <option value="Berline" <c:if test="${voiture.categorie eq 'Berline'}">selected</c:if>>Berline</option>
                    <option value="Luxe" <c:if test="${voiture.categorie eq 'Luxe'}">selected</c:if>>Luxe</option>
                    <option value="Utilitaire" <c:if test="${voiture.categorie eq 'Utilitaire'}">selected</c:if>>Utilitaire</option>
                </select>
            </div>
            <div>
                <label for="prixLocationJ" class="block text-sm font-medium text-gray-700">Prix de location/jour :</label>
                <input type="number" id="prixLocationJ" name="prixLocationJ" value="${voiture.prixLocationJ}" required min="0"
                       class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm">
            </div>

            <%-- Le champ statut n'est affiché et modifiable que si on est en mode édition (voiture != null) --%>
            <c:if test="${voiture != null}">
                <div>
                    <label for="statut" class="block text-sm font-medium text-gray-700">Statut :</label>
                    <select id="statut" name="statut" class="mt-1 block w-full px-3 py-2 border rounded-md shadow-sm" required>
                        <option value="Disponible" <c:if test="${voiture.statut eq 'Disponible'}">selected</c:if>>Disponible</option>
                        <option value="Louee" <c:if test="${voiture.statut eq 'Louee'}">selected</c:if>>Louée</option>
                        <option value="En maintenance" <c:if test="${voiture.statut eq 'En maintenance'}">selected</c:if>>En maintenance</option>
                    </select>
                </div>
            </c:if>

            <button type="submit" class="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700">
                <%-- Correction JSTL ici --%>
                <c:choose>
                    <c:when test="${voiture != null}">Modifier</c:when>
                    <c:otherwise>Ajouter</c:otherwise>
                </c:choose>
                Voiture
            </button>
            <a href="voitures" class="block text-center mt-2 text-gray-600 hover:underline">Retour à la liste</a>
        </form>
    </div>
</body>
</html>
