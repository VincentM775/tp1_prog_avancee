<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Connexion - MasterAnnonce</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="min-h-screen bg-gradient-to-br from-indigo-500 via-purple-500 to-pink-500 flex items-center justify-center p-4">
    <div class="w-full max-w-md">
        <!-- Logo -->
        <div class="text-center mb-8">
            <a href="${pageContext.request.contextPath}/" class="inline-flex items-center space-x-2">
                <svg class="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v4H7V8z"/>
                </svg>
                <span class="text-white font-bold text-2xl">MasterAnnonce</span>
            </a>
        </div>

        <!-- Card -->
        <div class="bg-white rounded-2xl shadow-2xl p-8">
            <h1 class="text-2xl font-bold text-gray-800 text-center mb-6">Connexion</h1>

            <!-- Error message -->
            <c:if test="${not empty error}">
                <div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6 flex items-center">
                    <svg class="w-5 h-5 mr-2 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
                    </svg>
                    <span>${error}</span>
                </div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/login" class="space-y-5">
                <!-- Username -->
                <div>
                    <label for="username" class="block text-sm font-medium text-gray-700 mb-1">
                        Nom d'utilisateur
                    </label>
                    <input type="text" id="username" name="username"
                           value="${username}"
                           required autofocus
                           class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
                </div>

                <!-- Password -->
                <div>
                    <label for="password" class="block text-sm font-medium text-gray-700 mb-1">
                        Mot de passe
                    </label>
                    <input type="password" id="password" name="password"
                           required
                           class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
                </div>

                <!-- Submit -->
                <button type="submit"
                        class="w-full bg-indigo-600 text-white py-3 px-4 rounded-lg font-semibold hover:bg-indigo-700 focus:ring-4 focus:ring-indigo-200 transition-colors">
                    Se connecter
                </button>
            </form>

            <!-- Register link -->
            <p class="text-center text-gray-600 mt-6">
                Pas encore de compte ?
                <a href="${pageContext.request.contextPath}/register" class="text-indigo-600 hover:text-indigo-700 font-medium">
                    S'inscrire
                </a>
            </p>
        </div>

        <!-- Back to home -->
        <p class="text-center mt-6">
            <a href="${pageContext.request.contextPath}/" class="text-white hover:text-indigo-200 text-sm">
                &larr; Retour à l'accueil
            </a>
        </p>
    </div>
</body>
</html>
