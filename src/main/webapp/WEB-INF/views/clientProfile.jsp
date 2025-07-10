<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%--
    Ce fichier JSP est un FRAGMENT de contenu pour l'onglet "Mon Profil".
    Il est inclus dynamiquement dans clientDashboard.jsp.
    Il ne doit PAS contenir de balises <html>, <head>, <body>, <title> ou de liens <link> CSS.
    Ces éléments sont gérés par clientDashboard.jsp.
--%>

<div class="main-content-card">
    <h2 class="text-2xl font-bold text-gray-800 mb-4 text-center">Mon Profil</h2>
    <p class="text-lg text-gray-600 text-center mb-8">Mettez à jour vos informations personnelles.</p>

    <%-- Messages de succès ou d'erreur --%>
    <c:if test="${not empty sessionScope.message}">
        <p class="success-message">${sessionScope.message}</p>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty requestScope.error}">
        <p class="error-message">${requestScope.error}</p>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <p class="error-message">${sessionScope.error}</p>
        <c:remove var="error" scope="session"/>
    </c:if>

    <form action="${pageContext.request.contextPath}/clientProfile" method="post" class="form-container">
        <input type="hidden" name="action" value="updateProfile"> <%-- Ajout d'une action pour le servlet --%>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="form-group">
                <label for="cin" class="form-label">CIN:</label>
                <input type="text" id="cin" name="cin" value="<c:out value="${sessionScope.client.cin}"/>" class="form-input" readonly>
            </div>
            <div class="form-group">
                <label for="nom" class="form-label">Nom:</label>
                <input type="text" id="nom" name="nom" value="<c:out value="${sessionScope.client.nom}"/>" class="form-input" required>
            </div>
            <div class="form-group">
                <label for="prenom" class="form-label">Prénom:</label>
                <input type="text" id="prenom" name="prenom" value="<c:out value="${sessionScope.client.prenom}"/>" class="form-input" required>
            </div>
            <div class="form-group">
                <label for="adresse" class="form-label">Adresse:</label>
                <input type="text" id="adresse" name="adresse" value="<c:out value="${sessionScope.client.adresse}"/>" class="form-input">
            </div>
            <div class="form-group">
                <label for="telephone" class="form-label">Téléphone:</label>
                <input type="tel" id="telephone" name="telephone" value="<c:out value="${sessionScope.client.telephone}"/>" class="form-input">
            </div>
            <div class="form-group">
                <label for="email" class="form-label">Email:</label>
                <input type="email" id="email" name="email" value="<c:out value="${sessionScope.client.email}"/>" class="form-input">
            </div>
            <div class="form-group">
                <label for="permis" class="form-label">Numéro Permis:</label>
                <input type="text" id="permis" name="numeroPermis" value="<c:out value="${sessionScope.client.permis}"/>" class="form-input"> <%-- CORRECTION ICI: name="numeroPermis" --%>
            </div>
            <div class="form-group">
                <label for="sexe" class="form-label">Sexe:</label>
                <select id="sexe" name="sexe" class="form-input">
                    <option value="">Sélectionner</option>
                    <option value="Homme" <c:if test="${sessionScope.client.sexe eq 'Homme'}">selected</c:if>>Homme</option>
                    <option value="Femme" <c:if test="${sessionScope.client.sexe eq 'Femme'}">selected</c:if>>Femme</option>
                </select>
            </div>
            <%-- Si tu as une date de délivrance du permis dans ton modèle Client, tu peux l'ajouter ici --%>
            <%-- <div class="form-group">
                <label for="dateDelivrancePermis" class="form-label">Date Délivrance Permis:</label>
                <input type="date" id="dateDelivrancePermis" name="dateDelivrancePermis" value="<fmt:formatDate value="${sessionScope.client.dateDelivrancePermis}" pattern="yyyy-MM-dd" />" class="form-input">
            </div> --%>
        </div>
        <div class="text-center mt-6">
            <button type="submit" class="btn-primary">
                <i class="fas fa-save mr-2"></i> Enregistrer les modifications
            </button>
        </div>
    </form>
</div>
