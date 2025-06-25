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
    <style>
        /* Styles spécifiques pour cette page si non couverts par style.css */
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f0f2f5;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            margin: 0;
        }
        .login-container {
            background-color: #ffffff;
            padding: 2.5rem;
            border-radius: 8px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 400px;
            text-align: center;
        }
        .login-container h2 {
            margin-bottom: 1.5rem;
            color: #333;
            font-size: 1.8rem;
        }
        .form-group {
            margin-bottom: 1rem;
            text-align: left;
        }
        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 600;
            color: #555;
        }
        .form-group input[type="text"],
        .form-group input[type="password"] {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 1rem;
            box-sizing: border-box; /* Assure que padding et border sont inclus dans la largeur */
        }
        .btn-primary {
            width: 100%;
            padding: 10px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            font-size: 1.1rem;
            cursor: pointer;
            transition: background-color 0.3s ease;
            margin-top: 1.5rem;
        }
        .btn-primary:hover {
            background-color: #0056b3;
        }
        .error-message {
            color: #dc3545;
            margin-top: 1rem;
            font-size: 0.9rem;
        }
        .message-box {
            background-color: #fff3cd; /* Jaune clair */
            color: #856404; /* Jaune foncé */
            border: 1px solid #ffeeba;
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 15px;
            text-align: center;
        }
        .role-switch-link {
            display: block;
            margin-top: 1rem;
            color: #007bff;
            text-decoration: none;
            font-size: 0.9rem;
        }
        .role-switch-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <h2 class="text-2xl font-bold text-center mb-6">Connexion</h2>

        <%-- Affichage des messages d'erreur --%>
        <% if (request.getAttribute("error") != null) { %>
            <p class="error-message"><%= request.getAttribute("error") %></p>
        <% } %>

        <%-- Détermination du type de connexion (client ou personnel) --%>
        <c:set var="isClientLogin" value="${param.client != null && param.client eq 'true'}" />

        <c:choose>
            <c:when test="${isClientLogin}">
                <%-- Formulaire de connexion pour les clients --%>
                <h3>Connexion Client</h3>
                <form action="clientAuth" method="post" class="space-y-4">
                    <div class="form-group">
                        <label for="cin" class="block text-sm font-medium text-gray-700">Votre CIN :</label>
                        <input type="text" id="cin" name="cin" required
                               class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm">
                    </div>
                    <div class="form-group">
                        <label for="password_client" class="block text-sm font-medium text-gray-700">Mot de passe :</label>
                        <input type="password" id="password_client" name="password" required
                               class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm">
                    </div>
                    <button type="submit" class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 btn-primary">
                        Se connecter (Client)
                    </button>
                </form>
                <a href="login.jsp" class="role-switch-link">Connexion Personnel (Gestionnaire/Chef d'Agence)</a>
            </c:when>
            <c:otherwise>
                <%-- Formulaire de connexion pour le personnel (gestionnaire/chef d'agence) --%>
                <h3>Connexion Personnel</h3>
                <form action="auth" method="post" class="space-y-4">
                    <div>
                        <label for="username" class="block text-sm font-medium text-gray-700">Nom d'utilisateur :</label>
                        <input type="text" id="username" name="username" required
                               class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm">
                    </div>
                    <div>
                        <label for="password" class="block text-sm font-medium text-gray-700">Mot de passe :</label>
                        <input type="password" id="password" name="password" required
                               class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm">
                    </div>
                    <button type="submit" class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 btn-primary">
                        Se connecter (Personnel)
                    </button>
                </form>
                <a href="login.jsp?client=true" class="role-switch-link">Connexion Client</a>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
