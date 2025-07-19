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

/* Styles spécifiques pour cette page */

body {

margin: 0;

overflow: hidden; /* Empêche les barres de défilement */

}



/* CSS pour l'arrière-plan vidéo */

#video-background {

position: fixed;

right: 0;

bottom: 0;

min-width: 100%;

min-height: 100%;

width: auto;

height: auto;

z-index: -100;

object-fit: cover;

}



/* CSS pour l'overlay (filtre sombre sur la vidéo pour la lisibilité) */

.video-overlay {

position: fixed;

top: 0;

left: 0;

width: 100%;

height: 100%;

background-color: rgba(0, 0, 0, 0.5);

z-index: -99;

}


/* --- STYLE POUR LE TITRE FLOTTANT (comme sur ta photo) --- */

.page-title {

position: absolute;

top: 10%; /* Ajuste cette valeur pour monter ou descendre le titre */

left: 50%;

transform: translateX(-50%);

font-size: 3rem; /* Grande taille de police */

color: white;

font-weight: bold;

text-shadow: 2px 2px 8px rgba(0,0,0,0.7); /* Ombre pour la lisibilité */

z-index: 10;

text-align: center;

}


/* Styles pour le conteneur de connexion */

.login-container {

position: absolute;

top: 65%;

left: 50%;

transform: translate(-50%, -50%);

z-index: 10;

padding: 2.5rem 3rem;

border-radius: 1rem;

box-shadow: 0 10px 25px rgba(0, 0, 0, 0.25);

width: 90%;

max-width: 480px;

text-align: center;

background-color: rgba(255, 255, 255, 0.98);

backdrop-filter: blur(8px);

border: 1px solid rgba(229, 231, 235, 0.5);

animation: fadeInScale 0.5s ease-out forwards;

}


/* Styles pour les titres A L'INTERIEUR de la boite */

.login-container h3 {

font-size: 1.1rem;

color: #333;

font-weight: 600;

margin-top: 0;

margin-bottom: 2rem;

}



@keyframes fadeInScale {

from { opacity: 0; transform: translate(-50%, -50%) scale(0.95); }

to { opacity: 1; transform: translate(-50%, -50%) scale(1); }

}



@media (max-width: 480px) {

.login-container { padding: 2rem 1.5rem; }

.page-title { font-size: 2rem; top: 15%; }

}



.role-switch-link {

display: block;

margin-top: 1.5rem;

color: #4f46e5;

text-decoration: none;

font-size: 0.95rem;

font-weight: 500;

transition: color 0.2s ease;

}



.role-switch-link:hover {

color: #4338ca;

text-decoration: underline;

}

</style>

</head>

<body>



<video autoplay muted loop id="video-background">

<source src="${pageContext.request.contextPath}/videos/video1.mp4" type="video/mp4">

Votre navigateur ne supporte pas les vidéos HTML5.

</video>

<div class="video-overlay"></div>



<!-- LE TITRE EST ICI, EN DEHORS DE LA BOITE BLANCHE -->

<h1 class="page-title">Connectez-vous à votre espace</h1>
<div class="login-container">



<% if (request.getAttribute("error") != null) { %>

<p class="error-message"><%= request.getAttribute("error") %></p>

<% } %>

<% if (request.getAttribute("message") != null) { %>

<p class="success-message"><%= request.getAttribute("message") %></p>

<% } %>



<c:set var="isClientLogin" value="${param.client != null && param.client eq 'true'}" />



<c:choose>

<c:when test="${isClientLogin}">

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