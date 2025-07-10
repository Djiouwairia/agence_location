<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Erreur</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
<style>
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
	
	}    </style>

</head>
<body class="bg-gray-100 flex items-center justify-center min-h-screen">
<video autoplay muted loop id="video-background">
		<source src="${pageContext.request.contextPath}/videos/video3.mp4" type="video/mp4">
		Votre navigateur ne supporte pas les vidéos HTML5.
		
	</video>
    <div class="login-container"> <%-- Réutilisation du style container --%>
        <h2 class="text-2xl font-bold text-center mb-6">Une erreur est survenue !</h2>
        
        <p class="error-message">
            <c:choose>
                <c:when test="${not empty requestScope.errorMessage}">
                    ${requestScope.errorMessage}
                </c:when>
                <c:otherwise>
                    Désolé, quelque chose s'est mal passé. Veuillez réessayer plus tard.
                </c:otherwise>
            </c:choose>
        </p>
        <p class="text-center mt-4">
            <a href="<c:url value="/dashboard"/>" class="text-blue-600 hover:underline">Retour au tableau de bord</a>
        </p>
        <p class="text-center mt-2">
            <a href="<c:url value="/auth?action=logout"/>" class="text-red-600 hover:underline">Déconnexion</a>
        </p>
    </div>
</body>
</html>
