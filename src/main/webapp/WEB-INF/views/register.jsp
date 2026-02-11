<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inscription - MasterAnnonce</title>
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
            <h1 class="text-2xl font-bold text-gray-800 text-center mb-6">Inscription</h1>

            <!-- General error message -->
            <c:if test="${not empty errors.general}">
                <div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6 flex items-center">
                    <svg class="w-5 h-5 mr-2 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
                    </svg>
                    <span>${errors.general}</span>
                </div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/register" class="space-y-5">
                <!-- Username -->
                <div>
                    <label for="username" class="block text-sm font-medium text-gray-700 mb-1">
                        Nom d'utilisateur
                    </label>
                    <input type="text" id="username" name="username"
                           value="${formData.values.username}"
                           required minlength="3"
                           class="w-full px-4 py-3 border rounded-lg transition-colors
                                  ${not empty errors.username ? 'border-red-500 focus:ring-red-500 focus:border-red-500' : 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500'}">
                    <c:if test="${not empty errors.username}">
                        <p class="mt-1 text-sm text-red-600 flex items-center">
                            <svg class="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                                <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
                            </svg>
                            ${errors.username}
                        </p>
                    </c:if>
                </div>

                <!-- Email -->
                <div>
                    <label for="email" class="block text-sm font-medium text-gray-700 mb-1">
                        Email
                    </label>
                    <input type="email" id="email" name="email"
                           value="${formData.values.email}"
                           required
                           class="w-full px-4 py-3 border rounded-lg transition-colors
                                  ${not empty errors.email ? 'border-red-500 focus:ring-red-500 focus:border-red-500' : 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500'}">
                    <c:if test="${not empty errors.email}">
                        <p class="mt-1 text-sm text-red-600 flex items-center">
                            <svg class="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                                <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
                            </svg>
                            ${errors.email}
                        </p>
                    </c:if>
                </div>

                <!-- Password -->
                <div>
                    <label for="password" class="block text-sm font-medium text-gray-700 mb-1">
                        Mot de passe
                    </label>
                    <input type="password" id="password" name="password"
                           required minlength="6"
                           class="w-full px-4 py-3 border rounded-lg transition-colors
                                  ${not empty errors.password ? 'border-red-500 focus:ring-red-500 focus:border-red-500' : 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500'}">
                    <c:choose>
                        <c:when test="${not empty errors.password}">
                            <p class="mt-1 text-sm text-red-600 flex items-center">
                                <svg class="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                                    <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
                                </svg>
                                ${errors.password}
                            </p>
                        </c:when>
                        <c:otherwise>
                            <p class="mt-1 text-sm text-gray-500">Minimum 6 caractères</p>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Confirm Password -->
                <div>
                    <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">
                        Confirmer le mot de passe
                    </label>
                    <input type="password" id="confirmPassword" name="confirmPassword"
                           required
                           class="w-full px-4 py-3 border rounded-lg transition-colors
                                  ${not empty errors.confirmPassword ? 'border-red-500 focus:ring-red-500 focus:border-red-500' : 'border-gray-300 focus:ring-indigo-500 focus:border-indigo-500'}">
                    <c:if test="${not empty errors.confirmPassword}">
                        <p class="mt-1 text-sm text-red-600 flex items-center">
                            <svg class="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                                <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
                            </svg>
                            ${errors.confirmPassword}
                        </p>
                    </c:if>
                </div>

                <!-- Submit -->
                <button type="submit"
                        class="w-full bg-indigo-600 text-white py-3 px-4 rounded-lg font-semibold hover:bg-indigo-700 focus:ring-4 focus:ring-indigo-200 transition-colors">
                    S'inscrire
                </button>
            </form>

            <!-- Login link -->
            <p class="text-center text-gray-600 mt-6">
                Déjà un compte ?
                <a href="${pageContext.request.contextPath}/login" class="text-indigo-600 hover:text-indigo-700 font-medium">
                    Se connecter
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
