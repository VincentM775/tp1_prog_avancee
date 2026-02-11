<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${annonce.title} - MasterAnnonce</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="min-h-screen bg-gray-50 flex flex-col">
    <%@ include file="../includes/header.jsp" %>

    <main class="flex-grow">
        <div class="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <!-- Back link -->
            <a href="${pageContext.request.contextPath}/annonces"
               class="inline-flex items-center text-indigo-600 hover:text-indigo-700 mb-6">
                <svg class="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"/>
                </svg>
                Retour aux annonces
            </a>

            <!-- Main card -->
            <div class="bg-white rounded-xl shadow-md overflow-hidden">
                <!-- Header -->
                <div class="p-6 sm:p-8 border-b border-gray-100">
                    <div class="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-4">
                        <div class="flex-1">
                            <!-- Category -->
                            <c:if test="${annonce.category != null}">
                                <span class="inline-block px-3 py-1 text-xs font-medium bg-indigo-100 text-indigo-700 rounded-full mb-3">
                                    ${annonce.category.label}
                                </span>
                            </c:if>

                            <!-- Title -->
                            <h1 class="text-2xl sm:text-3xl font-bold text-gray-900">${annonce.title}</h1>
                        </div>

                        <!-- Status badge -->
                        <c:choose>
                            <c:when test="${annonce.status == 'DRAFT'}">
                                <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-yellow-100 text-yellow-800">
                                    <svg class="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z" clip-rule="evenodd"/>
                                    </svg>
                                    Brouillon
                                </span>
                            </c:when>
                            <c:when test="${annonce.status == 'PUBLISHED'}">
                                <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-green-100 text-green-800">
                                    <svg class="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"/>
                                    </svg>
                                    Publiée
                                </span>
                            </c:when>
                            <c:when test="${annonce.status == 'ARCHIVED'}">
                                <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-gray-100 text-gray-800">
                                    <svg class="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                                        <path d="M4 3a2 2 0 100 4h12a2 2 0 100-4H4z"/>
                                        <path fill-rule="evenodd" d="M3 8h14v7a2 2 0 01-2 2H5a2 2 0 01-2-2V8zm5 3a1 1 0 011-1h2a1 1 0 110 2H9a1 1 0 01-1-1z" clip-rule="evenodd"/>
                                    </svg>
                                    Archivée
                                </span>
                            </c:when>
                        </c:choose>
                    </div>

                    <!-- Meta info -->
                    <div class="flex flex-wrap gap-4 mt-4 text-sm text-gray-500">
                        <span class="flex items-center">
                            <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                            </svg>
                            <fmt:formatDate value="${annonce.date}" pattern="dd/MM/yyyy à HH:mm"/>
                        </span>
                        <c:if test="${annonce.author != null}">
                            <span class="flex items-center">
                                <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                                </svg>
                                Par ${annonce.author.username}
                            </span>
                        </c:if>
                    </div>
                </div>

                <!-- Description -->
                <div class="p-6 sm:p-8 border-b border-gray-100">
                    <h2 class="text-lg font-semibold text-gray-900 mb-3">Description</h2>
                    <p class="text-gray-600 whitespace-pre-line">
                        ${annonce.description != null ? annonce.description : 'Aucune description fournie.'}
                    </p>
                </div>

                <!-- Contact info -->
                <div class="p-6 sm:p-8 bg-gray-50">
                    <h2 class="text-lg font-semibold text-gray-900 mb-4">Informations de contact</h2>
                    <div class="space-y-3">
                        <c:if test="${annonce.adress != null}">
                            <div class="flex items-start">
                                <svg class="w-5 h-5 text-gray-400 mr-3 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
                                </svg>
                                <span class="text-gray-700">${annonce.adress}</span>
                            </div>
                        </c:if>
                        <c:if test="${annonce.mail != null}">
                            <div class="flex items-start">
                                <svg class="w-5 h-5 text-gray-400 mr-3 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/>
                                </svg>
                                <a href="mailto:${annonce.mail}" class="text-indigo-600 hover:text-indigo-700">
                                    ${annonce.mail}
                                </a>
                            </div>
                        </c:if>
                        <c:if test="${annonce.adress == null && annonce.mail == null}">
                            <p class="text-gray-500 italic">Aucune information de contact fournie.</p>
                        </c:if>
                    </div>
                </div>
            </div>

            <!-- Owner actions -->
            <c:if test="${isOwner}">
                <div class="mt-6 bg-white rounded-xl shadow-md p-6">
                    <h2 class="text-lg font-semibold text-gray-900 mb-4">Actions</h2>
                    <div class="flex flex-wrap gap-3">
                        <!-- Edit button -->
                        <c:if test="${annonce.status != 'ARCHIVED'}">
                            <a href="${pageContext.request.contextPath}/annonces/edit?id=${annonce.id}"
                               class="inline-flex items-center px-4 py-2 bg-indigo-600 text-white rounded-lg font-medium hover:bg-indigo-700 transition-colors">
                                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/>
                                </svg>
                                Modifier
                            </a>
                        </c:if>

                        <!-- Publish button -->
                        <c:if test="${annonce.status == 'DRAFT'}">
                            <form method="post" action="${pageContext.request.contextPath}/annonces/action" class="inline">
                                <input type="hidden" name="id" value="${annonce.id}">
                                <input type="hidden" name="action" value="publish">
                                <button type="submit"
                                        class="inline-flex items-center px-4 py-2 bg-green-600 text-white rounded-lg font-medium hover:bg-green-700 transition-colors">
                                    <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                                    </svg>
                                    Publier
                                </button>
                            </form>
                        </c:if>

                        <!-- Archive button -->
                        <c:if test="${annonce.status != 'ARCHIVED'}">
                            <form method="post" action="${pageContext.request.contextPath}/annonces/action" class="inline">
                                <input type="hidden" name="id" value="${annonce.id}">
                                <input type="hidden" name="action" value="archive">
                                <button type="submit"
                                        onclick="return confirm('Voulez-vous vraiment archiver cette annonce ?')"
                                        class="inline-flex items-center px-4 py-2 bg-yellow-500 text-white rounded-lg font-medium hover:bg-yellow-600 transition-colors">
                                    <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4"/>
                                    </svg>
                                    Archiver
                                </button>
                            </form>
                        </c:if>

                        <!-- Delete button -->
                        <form method="post" action="${pageContext.request.contextPath}/annonces/action" class="inline">
                            <input type="hidden" name="id" value="${annonce.id}">
                            <input type="hidden" name="action" value="delete">
                            <button type="submit"
                                    onclick="return confirm('Voulez-vous vraiment supprimer cette annonce ? Cette action est irréversible.')"
                                    class="inline-flex items-center px-4 py-2 bg-red-600 text-white rounded-lg font-medium hover:bg-red-700 transition-colors">
                                <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                                </svg>
                                Supprimer
                            </button>
                        </form>
                    </div>
                </div>
            </c:if>
        </div>
    </main>

    <%@ include file="../includes/footer.jsp" %>
</body>
</html>
