<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${isEdit ? 'Modifier' : 'Nouvelle'} annonce - MasterAnnonce</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="min-h-screen bg-gray-50 flex flex-col">
    <%@ include file="../includes/header.jsp" %>

    <main class="flex-grow">
        <div class="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            <!-- Title -->
            <div class="mb-8">
                <h1 class="text-3xl font-bold text-gray-900">
                    ${isEdit ? 'Modifier l\'annonce' : 'Nouvelle annonce'}
                </h1>
                <p class="mt-2 text-gray-600">
                    ${isEdit ? 'Modifiez les informations de votre annonce' : 'Remplissez le formulaire pour créer une nouvelle annonce'}
                </p>
            </div>

            <!-- General error -->
            <c:if test="${not empty errors.general}">
                <div class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6 flex items-center">
                    <svg class="w-5 h-5 mr-2 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
                    </svg>
                    <span>${errors.general}</span>
                </div>
            </c:if>

            <!-- Form Card -->
            <div class="bg-white rounded-xl shadow-md p-6 sm:p-8">
                <form method="post" class="space-y-6"
                      action="${pageContext.request.contextPath}/annonces/${isEdit ? 'edit?id='.concat(annonce.id) : 'new'}">

                    <!-- Title -->
                    <div>
                        <label for="title" class="block text-sm font-medium text-gray-700 mb-1">
                            Titre <span class="text-red-500">*</span>
                        </label>
                        <input type="text" id="title" name="title"
                               value="${not empty formData.values.title ? formData.values.title : (annonce != null ? annonce.title : '')}"
                               required maxlength="64"
                               class="w-full px-4 py-3 border rounded-lg transition-colors
                                      ${not empty errors.title ? 'border-red-500 focus:ring-red-500 focus:border-red-500' : 'border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500'}">
                        <c:choose>
                            <c:when test="${not empty errors.title}">
                                <p class="mt-1 text-sm text-red-600 flex items-center">
                                    <svg class="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                                        <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
                                    </svg>
                                    ${errors.title}
                                </p>
                            </c:when>
                            <c:otherwise>
                                <p class="mt-1 text-sm text-gray-500">Maximum 64 caractères</p>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Description -->
                    <div>
                        <label for="description" class="block text-sm font-medium text-gray-700 mb-1">
                            Description
                        </label>
                        <textarea id="description" name="description"
                                  rows="4" maxlength="256"
                                  class="w-full px-4 py-3 border rounded-lg transition-colors resize-none
                                         ${not empty errors.description ? 'border-red-500 focus:ring-red-500 focus:border-red-500' : 'border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500'}">${not empty formData.values.description ? formData.values.description : (annonce != null ? annonce.description : '')}</textarea>
                        <c:choose>
                            <c:when test="${not empty errors.description}">
                                <p class="mt-1 text-sm text-red-600 flex items-center">
                                    <svg class="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                                        <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
                                    </svg>
                                    ${errors.description}
                                </p>
                            </c:when>
                            <c:otherwise>
                                <p class="mt-1 text-sm text-gray-500">Maximum 256 caractères</p>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Category -->
                    <div>
                        <label for="categoryId" class="block text-sm font-medium text-gray-700 mb-1">
                            Catégorie
                        </label>
                        <select id="categoryId" name="categoryId"
                                class="w-full px-4 py-3 border rounded-lg transition-colors bg-white
                                       ${not empty errors.categoryId ? 'border-red-500 focus:ring-red-500 focus:border-red-500' : 'border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500'}">
                            <option value="">-- Aucune catégorie --</option>
                            <c:forEach var="cat" items="${categories}">
                                <c:set var="selectedCatId" value="${not empty formData.values.categoryId ? formData.values.categoryId : (annonce != null && annonce.category != null ? annonce.category.id : '')}"/>
                                <option value="${cat.id}" ${selectedCatId == cat.id ? 'selected' : ''}>
                                    ${cat.label}
                                </option>
                            </c:forEach>
                        </select>
                        <c:if test="${not empty errors.categoryId}">
                            <p class="mt-1 text-sm text-red-600 flex items-center">
                                <svg class="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                                    <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
                                </svg>
                                ${errors.categoryId}
                            </p>
                        </c:if>
                    </div>

                    <!-- Address -->
                    <div>
                        <label for="adress" class="block text-sm font-medium text-gray-700 mb-1">
                            Adresse
                        </label>
                        <input type="text" id="adress" name="adress"
                               value="${not empty formData.values.adress ? formData.values.adress : (annonce != null ? annonce.adress : '')}"
                               maxlength="64"
                               class="w-full px-4 py-3 border rounded-lg transition-colors
                                      ${not empty errors.adress ? 'border-red-500 focus:ring-red-500 focus:border-red-500' : 'border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500'}">
                        <c:if test="${not empty errors.adress}">
                            <p class="mt-1 text-sm text-red-600 flex items-center">
                                <svg class="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                                    <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
                                </svg>
                                ${errors.adress}
                            </p>
                        </c:if>
                    </div>

                    <!-- Email -->
                    <div>
                        <label for="mail" class="block text-sm font-medium text-gray-700 mb-1">
                            Email de contact
                        </label>
                        <input type="email" id="mail" name="mail"
                               value="${not empty formData.values.mail ? formData.values.mail : (annonce != null ? annonce.mail : '')}"
                               maxlength="64"
                               class="w-full px-4 py-3 border rounded-lg transition-colors
                                      ${not empty errors.mail ? 'border-red-500 focus:ring-red-500 focus:border-red-500' : 'border-gray-300 focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500'}">
                        <c:if test="${not empty errors.mail}">
                            <p class="mt-1 text-sm text-red-600 flex items-center">
                                <svg class="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                                    <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd"/>
                                </svg>
                                ${errors.mail}
                            </p>
                        </c:if>
                    </div>

                    <!-- Actions -->
                    <div class="flex flex-col sm:flex-row gap-3 pt-4">
                        <button type="submit"
                                class="flex-1 bg-indigo-600 text-white py-3 px-6 rounded-lg font-semibold hover:bg-indigo-700 focus:ring-4 focus:ring-indigo-200 transition-colors">
                            ${isEdit ? 'Enregistrer les modifications' : 'Créer l\'annonce'}
                        </button>
                        <a href="${pageContext.request.contextPath}/annonces"
                           class="flex-1 text-center bg-gray-100 text-gray-700 py-3 px-6 rounded-lg font-semibold hover:bg-gray-200 transition-colors">
                            Annuler
                        </a>
                    </div>
                </form>
            </div>

            <!-- Info notice -->
            <c:if test="${not isEdit}">
                <div class="mt-6 bg-blue-50 border border-blue-200 rounded-lg p-4 flex items-start">
                    <svg class="w-5 h-5 text-blue-500 mt-0.5 mr-3 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                        <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"/>
                    </svg>
                    <p class="text-sm text-blue-700">
                        L'annonce sera créée en tant que <strong>brouillon</strong>. Vous pourrez la publier ensuite depuis votre espace personnel.
                    </p>
                </div>
            </c:if>
        </div>
    </main>

    <%@ include file="../includes/footer.jsp" %>
</body>
</html>
