<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes annonces - MasterAnnonce</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="min-h-screen bg-gray-50 flex flex-col">
    <%@ include file="../includes/header.jsp" %>

    <main class="flex-grow">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <!-- Header -->
            <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-8">
                <div>
                    <h1 class="text-3xl font-bold text-gray-900">Mes annonces</h1>
                    <p class="mt-1 text-gray-600">${totalElements} annonce(s)</p>
                </div>
                <a href="${pageContext.request.contextPath}/annonces/new"
                   class="inline-flex items-center justify-center px-6 py-3 bg-indigo-600 text-white rounded-lg font-semibold hover:bg-indigo-700 transition-colors">
                    <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
                    </svg>
                    Nouvelle annonce
                </a>
            </div>

            <c:choose>
                <c:when test="${empty annonces}">
                    <!-- No announcements -->
                    <div class="bg-white rounded-xl shadow-md p-12 text-center">
                        <svg class="w-20 h-20 text-gray-300 mx-auto mb-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v4H7V8z"/>
                        </svg>
                        <h2 class="text-xl font-semibold text-gray-800 mb-2">Vous n'avez pas encore d'annonces</h2>
                        <p class="text-gray-500 mb-6">Commencez par créer votre première annonce.</p>
                        <a href="${pageContext.request.contextPath}/annonces/new"
                           class="inline-flex items-center px-6 py-3 bg-indigo-600 text-white rounded-lg font-semibold hover:bg-indigo-700 transition-colors">
                            <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
                            </svg>
                            Créer ma première annonce
                        </a>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Desktop Table -->
                    <div class="hidden md:block bg-white rounded-xl shadow-md overflow-hidden">
                        <table class="min-w-full divide-y divide-gray-200">
                            <thead class="bg-gray-50">
                                <tr>
                                    <th scope="col" class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                                        Titre
                                    </th>
                                    <th scope="col" class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                                        Catégorie
                                    </th>
                                    <th scope="col" class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                                        Statut
                                    </th>
                                    <th scope="col" class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                                        Date
                                    </th>
                                    <th scope="col" class="px-6 py-4 text-right text-xs font-semibold text-gray-500 uppercase tracking-wider">
                                        Actions
                                    </th>
                                </tr>
                            </thead>
                            <tbody class="bg-white divide-y divide-gray-200">
                                <c:forEach var="annonce" items="${annonces}">
                                    <tr class="hover:bg-gray-50 transition-colors">
                                        <td class="px-6 py-4">
                                            <a href="${pageContext.request.contextPath}/annonces/detail?id=${annonce.id}"
                                               class="text-gray-900 font-medium hover:text-indigo-600 transition-colors">
                                                ${annonce.title}
                                            </a>
                                        </td>
                                        <td class="px-6 py-4 text-gray-500">
                                            ${annonce.category != null ? annonce.category.label : '-'}
                                        </td>
                                        <td class="px-6 py-4">
                                            <c:choose>
                                                <c:when test="${annonce.status == 'DRAFT'}">
                                                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                                                        Brouillon
                                                    </span>
                                                </c:when>
                                                <c:when test="${annonce.status == 'PUBLISHED'}">
                                                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                                                        Publiée
                                                    </span>
                                                </c:when>
                                                <c:when test="${annonce.status == 'ARCHIVED'}">
                                                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
                                                        Archivée
                                                    </span>
                                                </c:when>
                                            </c:choose>
                                        </td>
                                        <td class="px-6 py-4 text-gray-500">
                                            <fmt:formatDate value="${annonce.date}" pattern="dd/MM/yyyy"/>
                                        </td>
                                        <td class="px-6 py-4 text-right">
                                            <div class="flex items-center justify-end space-x-2">
                                                <a href="${pageContext.request.contextPath}/annonces/detail?id=${annonce.id}"
                                                   class="text-indigo-600 hover:text-indigo-800 font-medium text-sm">
                                                    Voir
                                                </a>
                                                <c:if test="${annonce.status != 'ARCHIVED'}">
                                                    <a href="${pageContext.request.contextPath}/annonces/edit?id=${annonce.id}"
                                                       class="text-gray-600 hover:text-gray-800 font-medium text-sm">
                                                        Modifier
                                                    </a>
                                                </c:if>
                                                <c:if test="${annonce.status == 'DRAFT'}">
                                                    <form method="post" action="${pageContext.request.contextPath}/annonces/action" class="inline">
                                                        <input type="hidden" name="id" value="${annonce.id}">
                                                        <input type="hidden" name="action" value="publish">
                                                        <button type="submit" class="text-green-600 hover:text-green-800 font-medium text-sm">
                                                            Publier
                                                        </button>
                                                    </form>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <!-- Mobile Cards -->
                    <div class="md:hidden space-y-4">
                        <c:forEach var="annonce" items="${annonces}">
                            <div class="bg-white rounded-xl shadow-md p-4">
                                <div class="flex items-start justify-between mb-2">
                                    <a href="${pageContext.request.contextPath}/annonces/detail?id=${annonce.id}"
                                       class="text-gray-900 font-semibold hover:text-indigo-600">
                                        ${annonce.title}
                                    </a>
                                    <c:choose>
                                        <c:when test="${annonce.status == 'DRAFT'}">
                                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                                                Brouillon
                                            </span>
                                        </c:when>
                                        <c:when test="${annonce.status == 'PUBLISHED'}">
                                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                                                Publiée
                                            </span>
                                        </c:when>
                                        <c:when test="${annonce.status == 'ARCHIVED'}">
                                            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
                                                Archivée
                                            </span>
                                        </c:when>
                                    </c:choose>
                                </div>
                                <div class="text-sm text-gray-500 mb-3">
                                    <span>${annonce.category != null ? annonce.category.label : 'Sans catégorie'}</span>
                                    <span class="mx-2">•</span>
                                    <span><fmt:formatDate value="${annonce.date}" pattern="dd/MM/yyyy"/></span>
                                </div>
                                <div class="flex items-center space-x-3 pt-3 border-t border-gray-100">
                                    <a href="${pageContext.request.contextPath}/annonces/detail?id=${annonce.id}"
                                       class="text-indigo-600 hover:text-indigo-800 text-sm font-medium">
                                        Voir
                                    </a>
                                    <c:if test="${annonce.status != 'ARCHIVED'}">
                                        <a href="${pageContext.request.contextPath}/annonces/edit?id=${annonce.id}"
                                           class="text-gray-600 hover:text-gray-800 text-sm font-medium">
                                            Modifier
                                        </a>
                                    </c:if>
                                    <c:if test="${annonce.status == 'DRAFT'}">
                                        <form method="post" action="${pageContext.request.contextPath}/annonces/action" class="inline">
                                            <input type="hidden" name="id" value="${annonce.id}">
                                            <input type="hidden" name="action" value="publish">
                                            <button type="submit" class="text-green-600 hover:text-green-800 text-sm font-medium">
                                                Publier
                                            </button>
                                        </form>
                                    </c:if>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- Pagination -->
                    <c:if test="${totalPages > 1}">
                        <div class="mt-8 flex justify-center items-center space-x-4">
                            <c:if test="${hasPrevious}">
                                <a href="${pageContext.request.contextPath}/mes-annonces?page=${currentPage - 1}"
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
                                <a href="${pageContext.request.contextPath}/mes-annonces?page=${currentPage + 1}"
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
