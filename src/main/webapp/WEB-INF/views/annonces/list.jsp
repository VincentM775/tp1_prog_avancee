<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Annonces - MasterAnnonce</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="min-h-screen bg-gray-50 flex flex-col">
    <%@ include file="../includes/header.jsp" %>

    <main class="flex-grow">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <!-- Title -->
            <h1 class="text-3xl font-bold text-gray-900 mb-8">Annonces</h1>

            <!-- Search Form -->
            <form method="get" action="${pageContext.request.contextPath}/annonces" class="mb-8">
                <div class="bg-white rounded-xl shadow-md p-4 sm:p-6">
                    <div class="flex flex-col sm:flex-row gap-4">
                        <div class="flex-1">
                            <input type="text" name="keyword" placeholder="Rechercher..."
                                   value="${keyword}"
                                   class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
                        </div>
                        <div class="sm:w-64">
                            <select name="category"
                                    class="w-full px-4 py-3 border border-gray-300 rounded-lg bg-white focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500 transition-colors">
                                <option value="">Toutes les catégories</option>
                                <c:forEach var="cat" items="${categories}">
                                    <option value="${cat.id}" ${selectedCategory == cat.id ? 'selected' : ''}>
                                        ${cat.label}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <button type="submit"
                                class="bg-indigo-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-indigo-700 transition-colors flex items-center justify-center">
                            <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/>
                            </svg>
                            Rechercher
                        </button>
                    </div>
                </div>
            </form>

            <!-- Results count -->
            <p class="text-gray-600 mb-6">${totalElements} annonce(s) trouvée(s)</p>

            <c:choose>
                <c:when test="${empty annonces}">
                    <!-- No results -->
                    <div class="bg-white rounded-xl shadow-md p-12 text-center">
                        <svg class="w-16 h-16 text-gray-300 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
                        </svg>
                        <p class="text-gray-500 text-lg">Aucune annonce trouvée.</p>
                        <p class="text-gray-400 mt-2">Essayez avec d'autres critères de recherche.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Grid -->
                    <div class="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
                        <c:forEach var="annonce" items="${annonces}">
                            <div class="bg-white rounded-xl shadow-md hover:shadow-lg transition-shadow overflow-hidden">
                                <div class="p-6">
                                    <!-- Category badge -->
                                    <c:if test="${annonce.category != null}">
                                        <span class="inline-block px-3 py-1 text-xs font-medium bg-indigo-100 text-indigo-700 rounded-full mb-3">
                                            ${annonce.category.label}
                                        </span>
                                    </c:if>

                                    <!-- Title -->
                                    <h3 class="text-lg font-semibold text-gray-900 mb-2">
                                        <a href="${pageContext.request.contextPath}/annonces/detail?id=${annonce.id}"
                                           class="hover:text-indigo-600 transition-colors">
                                            ${annonce.title}
                                        </a>
                                    </h3>

                                    <!-- Description -->
                                    <p class="text-gray-600 text-sm mb-4 line-clamp-3">
                                        ${annonce.description != null && annonce.description.length() > 120
                                            ? annonce.description.substring(0, 120).concat('...')
                                            : annonce.description}
                                    </p>

                                    <!-- Meta -->
                                    <div class="flex items-center justify-between text-sm text-gray-500 pt-4 border-t border-gray-100">
                                        <span class="flex items-center">
                                            <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                                            </svg>
                                            <fmt:formatDate value="${annonce.date}" pattern="dd/MM/yyyy"/>
                                        </span>
                                        <c:if test="${annonce.adress != null}">
                                            <span class="flex items-center">
                                                <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
                                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
                                                </svg>
                                                ${annonce.adress.length() > 20 ? annonce.adress.substring(0, 20).concat('...') : annonce.adress}
                                            </span>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <div class="mt-8 flex justify-center items-center space-x-4">
                            <c:if test="${hasPrevious}">
                                <a href="${pageContext.request.contextPath}/annonces?page=${currentPage - 1}&keyword=${keyword}&category=${selectedCategory}"
                                   class="px-4 py-2 bg-white border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors flex items-center">
                                    <svg class="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
                                    </svg>
                                    Précédent
                                </a>
                            </c:if>

                            <span class="text-gray-600">
                                Page <span class="font-semibold">${currentPage + 1}</span> sur <span class="font-semibold">${totalPages}</span>
                            </span>

                            <c:if test="${hasNext}">
                                <a href="${pageContext.request.contextPath}/annonces?page=${currentPage + 1}&keyword=${keyword}&category=${selectedCategory}"
                                   class="px-4 py-2 bg-white border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors flex items-center">
                                    Suivant
                                    <svg class="w-5 h-5 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
                                    </svg>
                                </a>
                            </c:if>
                        </div>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <%@ include file="../includes/footer.jsp" %>
</body>
</html>
