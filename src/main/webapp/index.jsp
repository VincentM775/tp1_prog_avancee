<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MasterAnnonce - Accueil</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="min-h-screen bg-gray-50 flex flex-col">
    <!-- Header -->
    <header class="bg-gradient-to-r from-indigo-600 to-purple-600 shadow-lg">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div class="flex justify-between items-center py-4">
                <a href="${pageContext.request.contextPath}/" class="flex items-center space-x-2">
                    <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v4H7V8z"/>
                    </svg>
                    <span class="text-white font-bold text-xl">MasterAnnonce</span>
                </a>
                <nav class="flex items-center space-x-4">
                    <a href="${pageContext.request.contextPath}/annonces"
                       class="text-white hover:text-indigo-200 transition-colors font-medium">
                        Annonces
                    </a>
                    <c:choose>
                        <c:when test="${not empty sessionScope.user}">
                            <a href="${pageContext.request.contextPath}/mes-annonces"
                               class="text-white hover:text-indigo-200 transition-colors font-medium">
                                Mes annonces
                            </a>
                            <span class="text-indigo-200 text-sm">Bonjour, ${sessionScope.username}</span>
                            <a href="${pageContext.request.contextPath}/logout"
                               class="text-indigo-200 hover:text-white text-sm transition-colors">
                                Déconnexion
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/login"
                               class="text-white hover:text-indigo-200 transition-colors font-medium">
                                Connexion
                            </a>
                            <a href="${pageContext.request.contextPath}/register"
                               class="bg-white text-indigo-600 px-4 py-2 rounded-lg font-semibold hover:bg-indigo-50 transition-colors">
                                S'inscrire
                            </a>
                        </c:otherwise>
                    </c:choose>
                </nav>
            </div>
        </div>
    </header>

    <!-- Hero Section -->
    <main class="flex-grow">
        <div class="bg-gradient-to-br from-indigo-500 via-purple-500 to-pink-500 text-white py-20">
            <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
                <h1 class="text-5xl font-extrabold tracking-tight mb-6">
                    Bienvenue sur MasterAnnonce
                </h1>
                <p class="text-xl text-indigo-100 max-w-2xl mx-auto mb-10">
                    La plateforme d'annonces moderne et simple d'utilisation.
                    Publiez, recherchez et gérez vos annonces en toute simplicité.
                </p>
                <div class="flex justify-center gap-4 flex-wrap">
                    <a href="${pageContext.request.contextPath}/annonces"
                       class="bg-white text-indigo-600 px-8 py-3 rounded-lg font-bold text-lg hover:bg-indigo-50 transition-colors shadow-lg">
                        Parcourir les annonces
                    </a>
                    <c:choose>
                        <c:when test="${not empty sessionScope.user}">
                            <a href="${pageContext.request.contextPath}/annonces/new"
                               class="bg-indigo-700 text-white px-8 py-3 rounded-lg font-bold text-lg hover:bg-indigo-800 transition-colors shadow-lg border border-indigo-400">
                                Publier une annonce
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/register"
                               class="bg-indigo-700 text-white px-8 py-3 rounded-lg font-bold text-lg hover:bg-indigo-800 transition-colors shadow-lg border border-indigo-400">
                                Créer un compte
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Features Section -->
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
            <h2 class="text-3xl font-bold text-center text-gray-800 mb-12">Fonctionnalités</h2>
            <div class="grid md:grid-cols-3 gap-8">
                <!-- Feature 1 -->
                <div class="bg-white rounded-xl shadow-md p-8 text-center hover:shadow-lg transition-shadow">
                    <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                        <svg class="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
                        </svg>
                    </div>
                    <h3 class="text-xl font-semibold text-gray-800 mb-2">Publier</h3>
                    <p class="text-gray-600">Créez et publiez vos annonces en quelques clics avec notre interface intuitive.</p>
                </div>

                <!-- Feature 2 -->
                <div class="bg-white rounded-xl shadow-md p-8 text-center hover:shadow-lg transition-shadow">
                    <div class="w-16 h-16 bg-indigo-100 rounded-full flex items-center justify-center mx-auto mb-4">
                        <svg class="w-8 h-8 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
                        </svg>
                    </div>
                    <h3 class="text-xl font-semibold text-gray-800 mb-2">Rechercher</h3>
                    <p class="text-gray-600">Trouvez facilement ce que vous cherchez avec les filtres et la recherche avancée.</p>
                </div>

                <!-- Feature 3 -->
                <div class="bg-white rounded-xl shadow-md p-8 text-center hover:shadow-lg transition-shadow">
                    <div class="w-16 h-16 bg-orange-100 rounded-full flex items-center justify-center mx-auto mb-4">
                        <svg class="w-8 h-8 text-orange-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"/>
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                        </svg>
                    </div>
                    <h3 class="text-xl font-semibold text-gray-800 mb-2">Gérer</h3>
                    <p class="text-gray-600">Modifiez, publiez ou archivez vos annonces depuis votre espace personnel.</p>
                </div>
            </div>
        </div>
    </main>

    <!-- Footer -->
    <footer class="bg-gray-800 mt-auto">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <div class="flex flex-col md:flex-row justify-between items-center">
                <div class="text-gray-400 text-sm">
                    &copy; 2025 MasterAnnonce - TP JPA/Hibernate
                </div>
                <div class="flex space-x-6 mt-4 md:mt-0">
                    <a href="#" class="text-gray-400 hover:text-white transition-colors text-sm">
                        Mentions légales
                    </a>
                    <a href="#" class="text-gray-400 hover:text-white transition-colors text-sm">
                        Contact
                    </a>
                </div>
            </div>
        </div>
    </footer>
</body>
</html>
