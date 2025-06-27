<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion - Agence de Location de Voitures</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        /* Styles spécifiques pour cette page si non couverts par style.css, ou pour overrides */
        body {
            /* Assurez-vous que le body de login.jsp a ces styles pour le centrage */
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            margin: 0;
            background-color: #f4f7f6; /* Utilisez le même fond que le reste de l'app */
        }
        .login-container {
            padding: 2.5rem; /* p-10 */
            border-radius: 12px; /* rounded-xl */
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1); /* shadow-xl */
            width: 100%;
            max-width: 450px; /* Plus large pour un meilleur espacement */
            text-align: center;
            background-color: #ffffff; /* Fond blanc pour le conteneur */
            border: 1px solid #e2e8f0; /* Bordure subtile */
        }
        .login-container h2 {
            font-size: 2.2rem; /* text-3xl */
            font-weight: 700; /* font-bold */
            color: #1a202c; /* text-gray-900 */
            margin-bottom: 2rem; /* mb-8 */
        }
        .login-container h3 {
            font-size: 1.5rem; /* text-2xl */
            font-weight: 600; /* font-semibold */
            color: #2d3748; /* text-gray-800 */
            margin-bottom: 1.5rem; /* mb-6 */
        }
        /* Les styles pour form-group, input[type="text"], input[type="password"], btn-primary, error-message, success-message sont déjà dans style.css */
        .role-switch-link {
            display: block;
            margin-top: 1.5rem; /* Plus d'espace */
            color: #4f46e5; /* indigo-600 */
            text-decoration: none;
            font-size: 0.95rem; /* Légèrement plus grand */
            font-weight: 500;
            transition: color 0.2s ease;
        }
        .role-switch-link:hover {
            color: #4338ca; /* indigo-700 */
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <h2 class="text-2xl font-bold text-center mb-6">Connexion</h2>

        <%-- Affichage des messages d'erreur ou de succès --%>
        <% if (request.getAttribute("error") != null) { %>
            <p class="error-message"><%= request.getAttribute("error") %></p>
        <% } %>
        <% if (request.getAttribute("message") != null) { %>
            <p class="success-message"><%= request.getAttribute("message") %></p>
        <% } %>


        <%-- Détermination du type de connexion (client ou personnel) --%>
        <c:set var="isClientLogin" value="${param.client != null && param.client eq 'true'}" />

        <c:choose>
            <c:when test="${isClientLogin}">
                <%-- Formulaire de connexion pour les clients --%>
                <h3>Connexion Client</h3>
                <form action="clientAuth" method="post">
                    <div class="form-group">
                        <label for="cin">Votre CIN :</label>
                        <input type="text" id="cin" name="cin" required>
                    </div>
                    <div class="form-group">
                        <label for="password_client">Mot de passe :</label>
                        <input type="password" id="password_client" name="password" required>
                    </div>
                    <button type="submit" class="btn-primary">
                        <i class="fas fa-sign-in-alt mr-2"></i> Se connecter (Client)
                    </button>
                </form>
                <a href="login.jsp" class="role-switch-link">Connexion Personnel</a>
            </c:when>
            <c:otherwise>
                <%-- Formulaire de connexion pour le personnel (gestionnaire/chef d'agence) --%>
                <h3>Connexion Personnel</h3>
                <form action="auth" method="post">
                    <div class="form-group">
                        <label for="username">Nom d'utilisateur :</label>
                        <input type="text" id="username" name="username" required>
                    </div>
                    <div class="form-group">
                        <label for="password">Mot de passe :</label>
                        <input type="password" id="password" name="password" required>
                    </div>
                    <button type="submit" class="btn-primary">
                        <i class="fas fa-sign-in-alt mr-2"></i> Se connecter (Personnel)
                    </button>
                </form>
                <a href="login.jsp?client=true" class="role-switch-link">Connexion Client</a>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
