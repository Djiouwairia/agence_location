<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Erreur</title>
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
</head>
<body class="bg-gray-100 flex items-center justify-center min-h-screen">
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
