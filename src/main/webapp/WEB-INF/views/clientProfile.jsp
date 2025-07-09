<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mon Profil - Agence de Location</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
</head>
<body>
    <jsp:include page="navbar.jsp"/>

    <div class="content-area">
        <main class="main-content-card">
            <h2 class="dashboard-heading text-3xl font-bold text-gray-800 mb-4 text-center">Mon Profil</h2>
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
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div class="form-group">
                        <label for="cin" class="form-label">CIN:</label>
                        <input type="text" id="cin" name="cin" value="${client.cin}" class="form-input" readonly>
                    </div>
                    <div class="form-group">
                        <label for="nom" class="form-label">Nom:</label>
                        <input type="text" id="nom" name="nom" value="${client.nom}" class="form-input" required>
                    </div>
                    <div class="form-group">
                        <label for="prenom" class="form-label">Prénom:</label>
                        <input type="text" id="prenom" name="prenom" value="${client.prenom}" class="form-input" required>
                    </div>
                    <div class="form-group">
                        <label for="adresse" class="form-label">Adresse:</label>
                        <input type="text" id="adresse" name="adresse" value="${client.adresse}" class="form-input">
                    </div>
                    <div class="form-group">
                        <label for="telephone" class="form-label">Téléphone:</label>
                        <input type="tel" id="telephone" name="telephone" value="${client.telephone}" class="form-input">
                    </div>
                    <div class="form-group">
                        <label for="email" class="form-label">Email:</label>
                        <input type="email" id="email" name="email" value="${client.email}" class="form-input">
                    </div>
                    <div class="form-group">
                        <label for="numeroPermis" class="form-label">Numéro Permis:</label>
                        <input type="text" id="numeroPermis" name="numeroPermis" value="${client.numeroPermis}" class="form-input">
                    </div>
                    <div class="form-group">
                        <label for="dateDelivrancePermis" class="form-label">Date Délivrance Permis:</label>
                        <input type="date" id="dateDelivrancePermis" name="dateDelivrancePermis" value="<fmt:formatDate value="${client.dateDelivrancePermis}" pattern="yyyy-MM-dd" />" class="form-input">
                    </div>
                </div>
                <div class="text-center mt-6">
                    <button type="submit" class="btn-primary">
                        <i class="fas fa-save mr-2"></i> Enregistrer les modifications
                    </button>
                </div>
            </form>
        </main>
    </div>
</body>
</html>
