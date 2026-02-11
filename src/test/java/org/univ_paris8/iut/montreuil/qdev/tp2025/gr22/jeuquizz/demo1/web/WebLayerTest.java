package org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.filter.AuthFilter;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.validation.FormData;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.validation.FormValidator;
import org.univ_paris8.iut.montreuil.qdev.tp2025.gr22.jeuquizz.demo1.web.validation.ValidationResult;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests de la couche Web - NIVEAU 4
 *
 * Ces tests vérifient :
 * 1. Le filtre d'authentification (AuthFilter)
 * 2. Les classes de validation (FormValidator, ValidationResult, FormData)
 * 3. Les comportements des servlets avec mocks HTTP
 */
@ExtendWith(MockitoExtension.class)
class WebLayerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private FilterChain filterChain;

    @Mock
    private RequestDispatcher requestDispatcher;

    // ==================== TESTS AUTH FILTER ====================

    @Nested
    @DisplayName("Tests du filtre d'authentification")
    class AuthFilterTests {

        private AuthFilter authFilter;

        @BeforeEach
        void setUp() throws ServletException {
            authFilter = new AuthFilter();
            authFilter.init(null);
        }

        @Test
        @DisplayName("Utilisateur connecté : accès autorisé")
        void testAuthenticatedUserAllowed() throws IOException, ServletException {
            // Simuler un utilisateur connecté
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute("user")).thenReturn(new Object()); // User simulé

            // Exécuter le filtre
            authFilter.doFilter(request, response, filterChain);

            // Vérifier que la chaîne continue (accès autorisé)
            verify(filterChain, times(1)).doFilter(request, response);
            verify(response, never()).sendRedirect(anyString());
        }

        @Test
        @DisplayName("Utilisateur non connecté : redirection vers login")
        void testUnauthenticatedUserRedirected() throws IOException, ServletException {
            // Simuler un utilisateur non connecté (pas de session)
            when(request.getSession(false)).thenReturn(null);
            // IMPORTANT: Mocker getSession(true) pour éviter NPE
            when(request.getSession(true)).thenReturn(session);
            when(request.getContextPath()).thenReturn("/app");
            when(request.getRequestURI()).thenReturn("/app/annonces/new");

            // Exécuter le filtre
            authFilter.doFilter(request, response, filterChain);

            // Vérifier la redirection vers login
            verify(response, times(1)).sendRedirect("/app/login");
            verify(filterChain, never()).doFilter(request, response);
        }

        @Test
        @DisplayName("Session existe mais pas d'utilisateur : redirection")
        void testSessionWithoutUserRedirected() throws IOException, ServletException {
            // Simuler une session sans utilisateur
            when(request.getSession(false)).thenReturn(session);
            when(session.getAttribute("user")).thenReturn(null);
            // IMPORTANT: Mocker getSession(true) pour éviter NPE
            when(request.getSession(true)).thenReturn(session);
            when(request.getContextPath()).thenReturn("/app");
            when(request.getRequestURI()).thenReturn("/app/mes-annonces");

            // Exécuter le filtre
            authFilter.doFilter(request, response, filterChain);

            // Vérifier la redirection
            verify(response, times(1)).sendRedirect("/app/login");
        }

        @Test
        @DisplayName("URL de redirection sauvegardée en session")
        void testRedirectUrlSaved() throws IOException, ServletException {
            // Simuler un utilisateur non connecté
            when(request.getSession(false)).thenReturn(null);
            when(request.getSession(true)).thenReturn(session);
            when(request.getContextPath()).thenReturn("/app");
            when(request.getRequestURI()).thenReturn("/app/annonces/new");

            // Exécuter le filtre
            authFilter.doFilter(request, response, filterChain);

            // Vérifier que l'URL est sauvegardée pour redirection après login
            verify(session, times(1)).setAttribute(eq("redirectAfterLogin"), eq("/app/annonces/new"));
        }
    }

    // ==================== TESTS VALIDATION ====================

    @Nested
    @DisplayName("Tests du système de validation")
    class ValidationTests {

        @Test
        @DisplayName("ValidationResult : ajout et récupération d'erreurs")
        void testValidationResult() {
            ValidationResult result = new ValidationResult();

            // Pas d'erreur au début
            assertFalse(result.hasErrors());
            assertEquals(0, result.getErrorCount());

            // Ajouter des erreurs
            result.addError("email", "Email invalide");
            result.addError("password", "Mot de passe trop court");

            // Vérifier les erreurs
            assertTrue(result.hasErrors());
            assertEquals(2, result.getErrorCount());
            assertTrue(result.hasError("email"));
            assertTrue(result.hasError("password"));
            assertFalse(result.hasError("username"));

            // Récupérer les messages
            assertEquals("Email invalide", result.getError("email"));
            assertEquals("Mot de passe trop court", result.getError("password"));
            assertNull(result.getError("username"));
        }

        @Test
        @DisplayName("FormValidator : validateRequired")
        void testValidateRequired() {
            ValidationResult result = new ValidationResult();

            // Valeur null
            FormValidator.validateRequired(result, "field1", null, "Champ requis");
            assertTrue(result.hasError("field1"));

            // Valeur vide
            FormValidator.validateRequired(result, "field2", "", "Champ requis");
            assertTrue(result.hasError("field2"));

            // Valeur avec espaces seulement
            FormValidator.validateRequired(result, "field3", "   ", "Champ requis");
            assertTrue(result.hasError("field3"));

            // Valeur valide
            FormValidator.validateRequired(result, "field4", "valeur", "Champ requis");
            assertFalse(result.hasError("field4"));
        }

        @Test
        @DisplayName("FormValidator : validateEmail")
        void testValidateEmail() {
            ValidationResult result = new ValidationResult();

            // Emails invalides
            FormValidator.validateEmail(result, "email1", "invalid", "Email invalide");
            assertTrue(result.hasError("email1"));

            FormValidator.validateEmail(result, "email2", "test@", "Email invalide");
            assertTrue(result.hasError("email2"));

            FormValidator.validateEmail(result, "email3", "@test.com", "Email invalide");
            assertTrue(result.hasError("email3"));

            // Email valide
            FormValidator.validateEmail(result, "email4", "user@example.com", "Email invalide");
            assertFalse(result.hasError("email4"));

            // Email vide (pas d'erreur, utiliser validateRequired pour ça)
            FormValidator.validateEmail(result, "email5", "", "Email invalide");
            assertFalse(result.hasError("email5"));
        }

        @Test
        @DisplayName("FormValidator : validateMinLength et validateMaxLength")
        void testValidateLengths() {
            ValidationResult result = new ValidationResult();

            // Min length
            FormValidator.validateMinLength(result, "pass1", "12345", 6, "Min 6 caractères");
            assertTrue(result.hasError("pass1"));

            FormValidator.validateMinLength(result, "pass2", "123456", 6, "Min 6 caractères");
            assertFalse(result.hasError("pass2"));

            // Max length
            FormValidator.validateMaxLength(result, "title1", "a".repeat(65), 64, "Max 64 caractères");
            assertTrue(result.hasError("title1"));

            FormValidator.validateMaxLength(result, "title2", "a".repeat(64), 64, "Max 64 caractères");
            assertFalse(result.hasError("title2"));
        }

        @Test
        @DisplayName("FormValidator : validateEquals")
        void testValidateEquals() {
            ValidationResult result = new ValidationResult();

            // Valeurs différentes
            FormValidator.validateEquals(result, "confirm1", "password1", "password2", "Non identiques");
            assertTrue(result.hasError("confirm1"));

            // Valeurs identiques
            FormValidator.validateEquals(result, "confirm2", "password", "password", "Non identiques");
            assertFalse(result.hasError("confirm2"));

            // Première valeur null
            FormValidator.validateEquals(result, "confirm3", null, "password", "Non identiques");
            assertTrue(result.hasError("confirm3"));
        }

        @Test
        @DisplayName("FormValidator : validateUsername")
        void testValidateUsername() {
            ValidationResult result = new ValidationResult();

            // Trop court (< 3)
            FormValidator.validateUsername(result, "user1", "ab", "Username invalide");
            assertTrue(result.hasError("user1"));

            // Caractères invalides
            FormValidator.validateUsername(result, "user2", "user@name", "Username invalide");
            assertTrue(result.hasError("user2"));

            // Valide (lettres, chiffres, underscore)
            FormValidator.validateUsername(result, "user3", "valid_user123", "Username invalide");
            assertFalse(result.hasError("user3"));
        }
    }

    // ==================== TESTS FORM DATA ====================

    @Nested
    @DisplayName("Tests de FormData")
    class FormDataTests {

        @Test
        @DisplayName("FormData : stockage et récupération")
        void testFormDataStorage() {
            FormData formData = new FormData();

            formData.set("title", "Mon titre");
            formData.set("description", "Ma description");
            formData.set("nullField", null);

            assertEquals("Mon titre", formData.get("title"));
            assertEquals("Ma description", formData.get("description"));
            assertEquals("", formData.get("nullField")); // null converti en ""
            assertEquals("", formData.get("inexistant")); // valeur par défaut
        }

        @Test
        @DisplayName("FormData : fromRequest")
        void testFormDataFromRequest() {
            // Simuler les paramètres de requête
            when(request.getParameter("title")).thenReturn("Titre test");
            when(request.getParameter("description")).thenReturn("Description test");
            when(request.getParameter("empty")).thenReturn(null);

            FormData formData = FormData.fromRequest(request, "title", "description", "empty");

            assertEquals("Titre test", formData.get("title"));
            assertEquals("Description test", formData.get("description"));
            assertEquals("", formData.get("empty"));
        }

        @Test
        @DisplayName("FormData : getValues pour JSP")
        void testFormDataGetValues() {
            FormData formData = new FormData();
            formData.set("field1", "value1");
            formData.set("field2", "value2");

            var values = formData.getValues();

            assertEquals(2, values.size());
            assertEquals("value1", values.get("field1"));
            assertEquals("value2", values.get("field2"));
        }
    }

    // ==================== TESTS SERVLET MOCK ====================

    @Nested
    @DisplayName("Tests des comportements Servlet")
    class ServletBehaviorTests {

        @Test
        @DisplayName("Forward vers une JSP")
        void testForwardToJsp() throws ServletException, IOException {
            when(request.getRequestDispatcher("/WEB-INF/views/login.jsp")).thenReturn(requestDispatcher);

            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);

            verify(requestDispatcher, times(1)).forward(request, response);
        }

        @Test
        @DisplayName("Redirect après action")
        void testRedirectAfterAction() throws IOException {
            when(request.getContextPath()).thenReturn("/app");

            response.sendRedirect(request.getContextPath() + "/annonces");

            verify(response, times(1)).sendRedirect("/app/annonces");
        }

        @Test
        @DisplayName("Attributs de requête")
        void testRequestAttributes() {
            ValidationResult errors = new ValidationResult();
            errors.addError("email", "Email invalide");

            request.setAttribute("errors", errors.getErrors());

            verify(request, times(1)).setAttribute(eq("errors"), any());
        }

        @Test
        @DisplayName("Session : stockage utilisateur")
        void testSessionUserStorage() {
            when(request.getSession(true)).thenReturn(session);

            HttpSession sess = request.getSession(true);
            sess.setAttribute("userId", 1L);
            sess.setAttribute("username", "testuser");

            verify(session, times(1)).setAttribute("userId", 1L);
            verify(session, times(1)).setAttribute("username", "testuser");
        }
    }
}
