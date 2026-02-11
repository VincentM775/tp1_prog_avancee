<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!-- Header avec Tailwind -->
<header class="bg-gradient-to-r from-indigo-600 to-purple-600 shadow-lg">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center py-4">
            <!-- Logo -->
            <a href="${pageContext.request.contextPath}/" class="flex items-center space-x-2">
                <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v4H7V8z"/>
                </svg>
                <span class="text-white font-bold text-xl">MasterAnnonce</span>
            </a>

            <!-- Navigation -->
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
                        <a href="${pageContext.request.contextPath}/annonces/new"
                           class="bg-white text-indigo-600 px-4 py-2 rounded-lg font-semibold hover:bg-indigo-50 transition-colors">
                            + Nouvelle
                        </a>
                        <div class="flex items-center space-x-3 ml-4 pl-4 border-l border-indigo-400">
                            <span class="text-indigo-200 text-sm">
                                <svg class="w-4 h-4 inline mr-1" fill="currentColor" viewBox="0 0 20 20">
                                    <path fill-rule="evenodd" d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" clip-rule="evenodd"/>
                                </svg>
                                ${sessionScope.username}
                            </span>
                            <a href="${pageContext.request.contextPath}/logout"
                               class="text-indigo-200 hover:text-white text-sm transition-colors">
                                Déconnexion
                            </a>
                        </div>
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

<!-- Messages flash -->
<c:if test="${not empty sessionScope.flashMessage}">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mt-4">
        <div class="rounded-lg p-4 ${sessionScope.flashType == 'success' ? 'bg-green-50 border border-green-200' : 'bg-red-50 border border-red-200'}">
            <div class="flex items-center">
                <c:choose>
                    <c:when test="${sessionScope.flashType == 'success'}">
                        <svg class="w-5 h-5 text-green-500 mr-3" fill="currentColor" viewBox="0 0 20 20">
                            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
                        </svg>
                        <span class="text-green-700">${sessionScope.flashMessage}</span>
                    </c:when>
                    <c:otherwise>
                        <svg class="w-5 h-5 text-red-500 mr-3" fill="currentColor" viewBox="0 0 20 20">
                            <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
                        </svg>
                        <span class="text-red-700">${sessionScope.flashMessage}</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    <c:remove var="flashMessage" scope="session"/>
    <c:remove var="flashType" scope="session"/>
</c:if>
